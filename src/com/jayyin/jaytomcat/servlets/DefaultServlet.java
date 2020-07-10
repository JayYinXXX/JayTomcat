package com.jayyin.jaytomcat.servlets;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.jayyin.jaytomcat.catalina.Context;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.WebXMLUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 默认入口，单例
 */
public class DefaultServlet extends HttpServlet {
    private static DefaultServlet instance = new DefaultServlet();
    //
    public DefaultServlet() {

    }

    //
    public static DefaultServlet getInstance() {
        return instance;
    }

    //
    @Override
    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;
        String uri = request.getUri();
        Context context = request.getContext();

        // 人为设置一个异常，测试500响应
        if (uri.equals("/500.html")) {
            throw new RuntimeException("this is a deliberately created exception");
        }
        // 访问目标文件
        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), fileName);
        if (file.exists()) {
            // 设置格式
            String extension = FileUtil.extName(file);
            String mimeType = WebXMLUtil.getMimeType(extension);
            response.setContentType(mimeType);
            // 二进制读取
            byte[] body = FileUtil.readBytes(file);
            response.setBody(body);

            // 模拟耗时任务，1s
            if (fileName.equals("timeConsume.html")) {
                ThreadUtil.sleep(1000);
            }

            // 200
            response.setStatus(Constant.CODE_200);
        }
        else {
            // 404
            response.setStatus(Constant.CODE_404);
        }
    }
}
