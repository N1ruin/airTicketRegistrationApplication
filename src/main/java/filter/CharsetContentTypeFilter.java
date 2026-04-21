package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class CharsetContentTypeFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;

        var path = request.getRequestURI().substring(request.getContextPath().length());
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (!path.startsWith("/webjars")) {
            servletResponse.setContentType("application/json");
        }

        filterChain.doFilter(request, servletResponse);
    }
}
