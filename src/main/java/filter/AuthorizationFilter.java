package filter;

import dto.user.UserDto;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (isFreePath(req)) {
            chain.doFilter(req, res);
            return;
        }

        var user = (UserDto) req.getSession().getAttribute("user");
        if (user == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (user.isBlocked()) {
            req.getSession().invalidate();
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isFreePath(HttpServletRequest req) {
        var path = req.getRequestURI()
                .substring(req.getContextPath()
                        .length());

        return path.equals("/signin") || path.equals("/signup") || path.contains("/webjars") || path.equals("/swagger");
    }
}
