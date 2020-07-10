package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.util.ArrayUtil;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

/**
 * Filter链
 *      每个Servlet对应一个Filter链
 */
public class ApplicationFilterChain implements FilterChain {
    private Filter[] filters;  // 链
    private Servlet servlet;
    int i;  //  等待访问的Filter索引
    //
    public ApplicationFilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = ArrayUtil.toArray(filters, Filter.class);
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if (i < filters.length) {
            Filter filter = filters[i];
            ++i;
            filter.doFilter(servletRequest, servletResponse, this);
        }
        // Filter链执行完访问Servlet
        else {
            servlet.service(servletRequest, servletResponse);
        }
    }
}
