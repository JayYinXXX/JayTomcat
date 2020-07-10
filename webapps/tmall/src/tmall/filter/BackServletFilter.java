package tmall.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

/**
 * 过滤器
 * 拦截并解析不同的URL，转到相应的Servlet和方法
 *
 * 后台，分类管理，list方法
 * admin_category_list
 */
public class BackServletFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getServletContext().getContextPath();
        String uri = request.getRequestURI();
        uri = StringUtils.remove(uri, contextPath);  // 剪去根目录及以上的路径

        if (uri.startsWith("/admin_")) {
            // 分解出目标Servlet的名字和方法名
            String servletPath = StringUtils.substringBetween(uri, "_", "_") + "Servlet";
            String method = StringUtils.substringAfterLast(uri, "_");
// test
            System.out.println("0.Filter BackServletFilter: to /" + servletPath + " " + method + "()");
            // 转发
            request.setAttribute("method", method);
            request.getRequestDispatcher("/" + servletPath).forward(request, response);

            // 转发成功则结束方法，不用找下一个filter
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
