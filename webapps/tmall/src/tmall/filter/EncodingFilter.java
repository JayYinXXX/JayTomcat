package tmall.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理中文的编码问题
 */
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        // 设置客户端提交的数据进行UTF-8编码
        request.setCharacterEncoding("UTF-8");
// test
        System.out.println("1.Filter EncodingFilter");
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
