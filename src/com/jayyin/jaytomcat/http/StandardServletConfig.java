package com.jayyin.jaytomcat.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet配置参数对象（解析web.xml文件获得），用于HttpServlet中的init()初始化
 */
public class StandardServletConfig implements ServletConfig {
    private ServletContext servletContext;
    private String servletName;
    private Map<String, String> initParameters;  // 参数集
    //
    public StandardServletConfig(ServletContext servletContext, String servletName, Map<String, String> initParameter) {
        this.servletContext = servletContext;
        this.servletName =servletName;
        this.initParameters = initParameter;
        if (this.initParameters == null) {
            this.initParameters = new HashMap<>();
        }
    }

    //
    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
}
