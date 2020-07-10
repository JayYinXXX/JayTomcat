package com.jayyin.jaytomcat.http;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 响应Response
 *      响应主体
 */
public class Response extends BaseResponse {
    private StringWriter stringWriter;
    private PrintWriter writer;
    private String contentType;
    private String redirect;  // 重定位url
    private int status;  // 状态码
    private byte[] body;  // 响应内容主体
    private byte[] responseBytes;  // 响应整体
    private List<Cookie> cookies;
    //
    public Response() {
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        contentType = "text/html;charset=utf-8;";
        cookies = new ArrayList<>();
    }


    // 返回响应主体数据
    public byte[] getBody() throws UnsupportedEncodingException {
        // 非二进制类型
        if (body == null) {
            String content = stringWriter.toString();
            body = content.getBytes("utf-8");
        }
        // 二进制类型
        return body;
    }

    // 将Cookie转化为首部格式
    public String getCookiesHeader() {
        if (cookies == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append("\r\n");
            sb.append("Set-Cookie: ");
            // key=value
            sb.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
            // expires到期时间
            if (cookie.getMaxAge() != -1) {
                // 计算过期时间，格式化成GMT时间
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
                Date expire = DateUtil.offset(new Date(), DateField.SECOND, cookie.getMaxAge());
                sb.append("expires=").append(sdf.format(expire)).append(";");
            }
            // path
            if (cookie.getPath() != null) {
                sb.append("path=").append(cookie.getPath());
            }
        }
        return sb.toString();
    }


    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void sendRedirect(String redirect) throws IOException {
        this.redirect = redirect;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    // Getter/Setter

    public void setBody(byte[] body) {
        this.body = body;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public String getRedirect() {
        return redirect;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(byte[] responseBytes) {
        this.responseBytes = responseBytes;
    }
}
