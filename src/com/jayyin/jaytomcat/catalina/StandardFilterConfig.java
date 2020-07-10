package com.jayyin.jaytomcat.catalina;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter配置
 */
public class StandardFilterConfig implements FilterConfig {
    private String filterName;
    private ServletContext servletContext;
    private Map<String, String> initParameters;
    //
    public StandardFilterConfig(String filterName, ServletContext servletContext, Map<String, String> initParameters) {
        this.filterName = filterName;
        this.servletContext = servletContext;
        this.initParameters = initParameters;
        if (initParameters == null) {
            this.initParameters = new HashMap<>();
        }
    }


    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }


    //
    @Override
    public String getFilterName() {
        return filterName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

}
