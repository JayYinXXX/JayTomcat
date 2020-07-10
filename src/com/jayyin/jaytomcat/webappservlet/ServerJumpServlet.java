package com.jayyin.jaytomcat.webappservlet;

import com.jayyin.jaytomcat.http.ApplicationRequestDispatcher;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServerJumpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("name", "jay");
        request.getRequestDispatcher("/hello").forward(request, response);
    }
}
