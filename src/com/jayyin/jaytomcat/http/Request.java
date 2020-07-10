package com.jayyin.jaytomcat.http;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.system.RuntimeInfo;
import com.jayyin.jaytomcat.Bootstrap;
import com.jayyin.jaytomcat.catalina.Context;
import com.jayyin.jaytomcat.catalina.Host;
import com.jayyin.jaytomcat.catalina.Service;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.MiniBrowser;
import com.jayyin.jaytomcat.util.SessionManager;
import com.jayyin.jaytomcat.util.WebXMLUtil;

import javax.naming.Name;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.Principal;
import java.sql.Struct;
import java.util.*;

/**
 * 请求Request
 *
 * Request的构建将会
 *      1.解析出客户端请求中的项目名，通过Service扫描部署在本地的Context，找到访问的目标Context
 *      2.解析出请求中的方法名method
 *      3.解析出目标资源的uri
 */
public class Request extends BaseRequest {
    private Socket socket;
    private String requestString;   // 请求全部内容
    private String uri;
    private Context context;
    private Service service;
    private boolean forwarded;
    private Map<String, Object> attributesMap;  // 服务端跳转传参
    private String method;
    private String queryString;  // 查询字符串/参数字符串
    private Map<String, String[]> parameterMap;  // 保存参数
    private Map<String, String> headerMap;  // 保存首部信息
    private Cookie[] cookies;
    private HttpSession session;
    // Constructor
    public Request(Socket socket, String requestString, Service service) throws IOException {
        this.socket = socket;
        this.requestString = requestString;
        this.service = service;
        this.attributesMap = new HashMap<>();
        this.parameterMap = new HashMap<>();
        this.headerMap = new HashMap<>();

        // parseHttpRequest();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }

        parseMethod();
        parseUri();
        parseContext();

        // 截掉uri中的项目名前缀
        uri = StrUtil.removePrefix(uri, context.getPath());
        // 截掉之后uri为空串（例："/app1"），uri指向访问默认页面
        if (StrUtil.isEmpty(uri) || "/".equals(uri)) {
            uri = "/" + WebXMLUtil.getWelcomeFile(context);
        }

        parseParameters();
        parseHeaders();
        parseCookies();
        parseSession();
    }


    // 1. 解析Socket，得到requestString（已经移到BioEndpoint组件中）
    //private void parseHttpRequest() throws IOException {
    //    InputStream is = socket.getInputStream();
    //    byte[] bytes = MiniBrowser.readBytes(is, false);
    //    requestString = new String(bytes, "utf-8");
    //}

    // 2. 解析requestString，得到method
    private void parseMethod() {
        method = StrUtil.subBefore(requestString, " ", false);
    }

    // 3. 解析requestString，得到uri
    private void parseUri() {
        // 请求起始行
        String temp = StrUtil.subBetween(requestString, " ", " ");
        // 参数
        if (StrUtil.contains(temp, '?')) {
            temp = StrUtil.subBefore(temp, '?', false);
        }
        uri = temp;
    }

    // 4. 解析uri，得到并设置对应项目的Context
    private void parseContext() {
        // 先匹配只有项目名的uri "/app1"
        context = service.getEngine().getDefaultHost().getContext(uri);
        if (context != null) {
            return;
        }

        // 从uri中取出项目的path
        String path = StrUtil.subBetween(uri, "/", "/");
        if (path == null) {
            path = Constant.defaultWebPath;
        }
        else {
            path = "/" + path;
        }

        Host host = service.getEngine().getDefaultHost();
        context = host.getContext(path);
        // 如果项目不存在，则默认指向ROOT
        if (context == null) {
            context = host.getContext(Constant.defaultWebPath);
        }
    }

    // 5. 解析参数（未解码）
    private void parseParameters() {
        if ("GET".equals(getMethod())) {
            // 从uri中解析参数
            String temp = StrUtil.subBetween(requestString, " ", " ");
            if (StrUtil.contains(temp, '?')) {
                queryString = StrUtil.subAfter(temp, '?', false);
            }
        }
        if ("POST".equals(getMethod())) {
            // 参数在请求内容主体中
            queryString = StrUtil.subAfter(requestString, "\r\n\r\n", false);
        }

        // 解析
        if (queryString == null || queryString.length() == 0) {
            return;
        }
        else {
            queryString = URLUtil.decode(queryString);
            String[] parameters = queryString.split("&");
            for (String param : parameters) {
                String[] temp = param.split("=");
                String name = temp[0];
                String value = temp[1];
                // 加入map
                String[] values = parameterMap.get(name);
                if (values == null) {
                    values = new String[] {value};
                }
                else {
                    values = ArrayUtil.append(values, value);
                }
                parameterMap.put(name, values);
            }
        }
    }

    // 6. 解析首部
    public void parseHeaders() {
        StringReader stringReader = new StringReader(requestString);
        List<String> lines = new ArrayList<>();
        IoUtil.readLines(stringReader, lines);
        // 从第二行开始读取，到空行
        for (int i=1; i <= lines.size()-1; ++i) {
            String line = lines.get(i);
            if (line.length() == 0) {
                break;
            }
            String[] key_value = line.split(":");
            String key = key_value[0].toLowerCase();
            String value = key_value[1];
            headerMap.put(key, value);
        }
    }

    // 7. 从首部中解析Cookie
    public void parseCookies() {
        List<Cookie> cookieList = new ArrayList<>();
        String cookies = headerMap.get("cookie");
        if (cookies != null) {
            String[] pairs = cookies.split(";");
            for (String pair : pairs) {
                // 非空
                if (!StrUtil.isBlank(pair)) {
                    String[] temp = pair.split("=");
                    if (temp.length == 2 && !StrUtil.isBlank(temp[0]) && !StrUtil.isBlank(temp[1])) {
                        Cookie cookie = new Cookie(temp[0].trim(), temp[1].trim());
                        cookieList.add(cookie);
                    }
                }
            }
        }
        this.cookies = ArrayUtil.toArray(cookieList, Cookie.class);
    }

    // 8. 解析session，没有则创建
    public void parseSession() {
        String jsessionid = getJsessionidFromCookie();
        this.session = SessionManager.getSession(jsessionid, this);
    }
    // 从cookie中获取jsessionid
    public String getJsessionidFromCookie() {
        for (Cookie cookie : cookies) {
            if ("JSESSIONID".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String uri) {
        return new ApplicationRequestDispatcher(uri);
    }


    //
    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public String getRealPath(String path) {
        return getServletContext().getRealPath(path);
    }

    @Override
    public String getParameter(String name) {
        String[] value = parameterMap.get(name);
        if (value != null && value.length != 0) {
            return value[0];
        }
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }
    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public String getHeader(String name) {
        if (name == null) {
            return null;
        }
        else {
            return headerMap.get(name.toLowerCase());
        }
    }
    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headerMap.keySet());
    }

    @Override
    public String getLocalAddr() {
        return socket.getLocalAddress().getHostAddress();
    }

    @Override
    public String getLocalName() {
        return socket.getLocalAddress().getHostName();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public String getProtocol() {
        return "HTTP:/1.1";
    }

    @Override
    public String getRemoteAddr() {
        InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
        String temp = isa.getAddress().toString();
        return StrUtil.subAfter(temp, "/", false);
    }

    @Override
    public String getRemoteHost() {
        InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
        return isa.getHostName();
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return getHeader("host").trim();
    }

    @Override
    public int getServerPort() {
        return getLocalPort();
    }

    @Override
    public String getContextPath() {
        String result = this.context.getPath();
        if ("/".equals(result))
            return "";
        return result;
    }

    @Override
    public String getRequestURI() {
        return uri;
    }

    @Override
    public String getServletPath() {
        return uri;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        // 协议
        String scheme = getScheme();
        url.append(scheme + "://");
        // 主机+端口
        url.append(getServerName());
        int port = getServerPort();
        if (port < 0) {
            port = 80;  // 没有指定port默认80
        }
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(":" + port);
        }
        // uri
        url.append(getRequestURI());

        return url;
    }


    // Getter/Setter
    public Socket getSocket() {
        return socket;
    }

    public String getRequestString() {
        return requestString;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributesMap.put(name, o);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributesMap.keySet());
    }

    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }
}
