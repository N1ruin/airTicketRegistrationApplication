package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (isSignUpOrSignInPath(req) || isAuthenticated(req)) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isSignUpOrSignInPath(HttpServletRequest req) {
        var path = req.getRequestURI().substring(req.getContextPath().length());

        return path.equals("/sign-in") || path.equals("/sign-up");
    }

    private boolean isAuthenticated(HttpServletRequest req) {
        var session = req.getSession();

        return session != null && session.getAttribute("user") != null;
    }
}
