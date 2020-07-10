package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.servlets.DefaultServlet;
import com.jayyin.jaytomcat.servlets.InvokerServlet;
import com.jayyin.jaytomcat.servlets.JspServlet;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.SessionManager;
import com.jayyin.jaytomcat.util.WebXMLUtil;
import com.jayyin.jaytomcat.webappservlet.HelloServlet;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * 处理Http
 *      处理请求，组装Response，交给Socket输出流
 */
public class HttpProcessor {

    public void execute(Socket s, Request request, Response response) {
        try {
            System.out.println("客户端请求信息：\r\n" + request.getRequestString());
            Context context = request.getContext();
            String uri = request.getUri();

            // 没有解析出uri，直接返回
            if (uri == null) {
                return;
            }

            // System.out.println("uri:" + uri);
            // System.out.println(request.getRequestURL());
            // System.out.println(request.getHeader("host"));

            // 判断是否存在对应的Servlet
            String servletClassName = context.getServletClassName(uri);
            HttpServlet workingServlet;
            // 1. 访问Servlet
            if (servletClassName != null) {
                // 将请求交给InvokerServlet处理
                workingServlet = InvokerServlet.getInstance();
            }
            // 2. jsp
            else if (uri.endsWith(".jsp")) {
                workingServlet = JspServlet.getInstance();
            }
            // 3. 访问静态资源
            else {
                workingServlet = DefaultServlet.getInstance();
            }

            // 执行filter责任链
            List<Filter> filters = context.getMatchedFilters(uri);
            ApplicationFilterChain filterChain = new ApplicationFilterChain(filters, workingServlet);
            filterChain.doFilter(request, response);

            // 将session放入response的cookie中
            SessionManager.createCookieBySession(request.getSession(), request, response);


            if (request.isForwarded()) {
                return;
            }
            // 处理不同响应
            if (Constant.CODE_200 == response.getStatus()) {
                handle200(s, response);
            }
            else if (Constant.CODE_302 == response.getStatus()) {
                handle302(s, response);
            }
            else if (Constant.CODE_404 == response.getStatus()) {
                handle404(s, response, uri);
            }
        } catch (Exception e) {
            LogFactory.get().error(e);
            // 500
            handle500(s, response, e);
        }
    }

    // 处理不同response
    // 200
    private void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_200;
        headText = StrUtil.format(headText, contentType, response.getCookiesHeader());

        // 组合首部+响应体
        byte[] head = headText.getBytes();
        byte[] body = response.getBody();
        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        // 完成响应整体
        response.setResponseBytes(responseBytes);
    }
    // 302
    private void handle302(Socket s, Response response) throws IOException {
        String redirect = response.getRedirect();
        String headText = StrUtil.format(Constant.response_head_302, redirect);

        byte[] responseBytes = headText.getBytes();
        // 完成响应整体
        response.setResponseBytes(responseBytes);
    }
    // 404
    private void handle404(Socket s, Response response, String uri) throws IOException {
        String headText = Constant.response_head_404;
        String bodyText = "<html><head><title>JayTomcat/1.0.1 - Error report</title>" +
                        "</head><body><h1>HTTP Status 404 - {}</h1>" +
                        "<HR><p><b>type: </b>Status report</p><p><b>message: </b><u>{}</u></p><p><b>description: </b>" +
                        "<u>The requested resource is not available.</u></p><HR><h3>JayTomcat 1.0.1</h3>" +
                        "</body></html>";
        bodyText = StrUtil.format(bodyText, uri, uri);
        byte[] responseBytes = (headText + bodyText).getBytes("utf-8");

        // 完成响应整体
        response.setResponseBytes(responseBytes);
    }
    // 500
    private void handle500(Socket s, Response response, Exception e) {
        try {
            String headText = Constant.response_head_500;

            String bodyText = "<html><head><title>JayTomcat/1.0.1 - Error report</title></head><body>" +
                    "<h1>HTTP Status 500 - An exception occurred processing {}</h1><HR>" +
                    "<p><b>type: </b>Exception report</p>" +
                    "<p><b>message: </b><u>An exception occurred processing {}</u></p>" +
                    "<p><b>description: </b><u>The server encountered an internal error that prevented it from fulfilling this request.</u></p>" +
                    "<p>Stacktrace: </p><pre>{}</pre><HR>" +
                    "<h3>JayTomcat 1.0.1</h3></body></html>";
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            StringBuilder strb = new StringBuilder();
            strb.append(e.toString()).append("\r\n");
            for (StackTraceElement ste : stackTraceElements) {
                strb.append("\t").append(ste.toString()).append("\r\n");
            }

            String msg = e.getMessage();
            if (msg != null && msg.length() > 20) {
                msg = msg.substring(0, 20);
            }

            bodyText = StrUtil.format(bodyText, msg, e.toString(), strb.toString());
            byte[] responseBytes = (headText + bodyText).getBytes("utf-8");

            // 完成响应整体
            response.setResponseBytes(responseBytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
