package com.jayyin.jaytomcat.webappservlet;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) {
        // String author = config.getInitParameter("author");
        // System.out.println("HelloServlet初始化\nauthor: " + author);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().println("Hello JayTomcat from HelloServlet");

            String name = (String) request.getAttribute("name");
            if (name != null) {
                response.getWriter().println("name:" + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("HelloServlet运行结束");
    }
}
