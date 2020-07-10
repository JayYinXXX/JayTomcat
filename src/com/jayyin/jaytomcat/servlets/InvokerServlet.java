package com.jayyin.jaytomcat.servlets;

import cn.hutool.core.util.ReflectUtil;
import com.jayyin.jaytomcat.catalina.Context;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 访问Servlet的入口，单例
 */
public class InvokerServlet extends HttpServlet {
    private static InvokerServlet instance = new InvokerServlet();
    //
    private InvokerServlet() {

    }


    //
    public static InvokerServlet getInstance() {
        return instance;
    }

    // 执行目标Servlet任务
    @Override
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassName(uri);
        try {
            // 调用目标servlet单例
            Class<?> servletClass = context.getWebappClassLoader().loadClass(servletClassName);
            context.getServlet(servletClass).service(request, response);
            //System.out.println(servletClass + ": " + servletClass.getClassLoader());

            if (response.getRedirect() != null) {
                response.setStatus(Constant.CODE_302);
            }
            else {
                response.setStatus(Constant.CODE_200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
