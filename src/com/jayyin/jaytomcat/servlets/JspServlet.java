package com.jayyin.jaytomcat.servlets;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jayyin.jaytomcat.catalina.Context;
import com.jayyin.jaytomcat.classloader.JspClassLoader;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.JspUtil;
import com.jayyin.jaytomcat.util.WebXMLUtil;
import org.apache.jasper.JasperException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 处理jsp入口，单例
 */
public class JspServlet extends HttpServlet {
    private static JspServlet instance = new JspServlet();
    //
    private JspServlet() {

    }

    public static JspServlet getInstance() {
        return instance;
    }

    @Override
    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            Request request = (Request) httpServletRequest;
            Response response = (Response) httpServletResponse;

            // 定位文件
            String uri = request.getUri();
            Context context = request.getContext();
            String fileName = StrUtil.removePrefix(uri, "/");
            File jspFile = new File(context.getDocBase(), fileName);

            // 处理jsp文件
            if (jspFile.exists()) {
                // 编译成.java和.class文件存入/work目录下
                String subFolder = context.getPath();
                subFolder = StrUtil.subAfter(subFolder, "/", false);
                String servletClassPath = JspUtil.getServletClassPath(uri, subFolder);
                File servletClassFile = new File(servletClassPath);

                // 检查编译出的.class文件
                // 不存在
                if (!servletClassFile.exists() ) {
                    JspUtil.compileJsp(context, jspFile);
                }
                // 过时了
                else if (jspFile.lastModified() > servletClassFile.lastModified()) {
                    JspClassLoader.removeJspClassLoader(uri, context);
                    JspUtil.compileJsp(context, jspFile);
                }
                // 加载执行
                JspClassLoader jspClassLoader = JspClassLoader.getJspClassLoader(uri, context);
                String jspServletClassName = JspUtil.getJspServletClassName(uri, subFolder);
                Class<?> jspServletClass = jspClassLoader.loadClass(jspServletClassName);
                HttpServlet servlet = context.getServlet(jspServletClass);
                servlet.service(request, response);


                // 测试用，文本格式读取内容
                String extName = FileUtil.extName(jspFile);
                String mimeType = WebXMLUtil.getMimeType(extName);
                response.setContentType(mimeType);

                byte[] bytes = FileUtil.readBytes(jspFile);
                response.setBody(bytes);


                if (response.getRedirect() != null) {
                    response.setStatus(Constant.CODE_302);
                }
                else {
                    response.setStatus(Constant.CODE_200);
                }
            }
            else {
                response.setStatus(Constant.CODE_404);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
