package cn.how2j.javaweb;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().println("Hello DIY Tomcat from HelloServlet@javaweb " + this);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}