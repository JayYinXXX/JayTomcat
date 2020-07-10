package com.jayyin.jaytomcat.http;

import com.jayyin.jaytomcat.catalina.HttpProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 服务端跳转
 */
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String uri;
    //
    public ApplicationRequestDispatcher(String uri) {
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        this.uri = uri;
    }


    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request request = (Request) servletRequest;
        Response response = (Response) servletResponse;

        request.setUri(uri);

        // 转发
        HttpProcessor processor = new HttpProcessor();
        processor.execute(request.getSocket(), request, response);
        request.setForwarded(true);
    }

    // 转发后继续完成原来的响应
    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

    }
}
