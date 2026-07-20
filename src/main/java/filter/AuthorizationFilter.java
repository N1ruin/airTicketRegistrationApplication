package filter;

import domain.Role;
import dto.user.UserDto;
import exception.ApplicationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class AuthorizationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (isFreePath(req)) {
            chain.doFilter(req, res);
            return;
        }

        var session = req.getSession(false);
        if (session == null) {
            throw new ApplicationException("Authentication required", SC_UNAUTHORIZED);
        }

        var user = (UserDto) session.getAttribute("user");
        if (user == null) {
            throw new ApplicationException("Authentication required", SC_UNAUTHORIZED);
        }

        if (user.isBlocked()) {
            req.getSession().invalidate();
            throw new ApplicationException("You are blocked", SC_FORBIDDEN);
        }

        if (user.role() == Role.ADMIN && isOnlyUserPath(req)) {
            throw new ApplicationException("You do not have access rights to user resources", SC_FORBIDDEN);
        }

        if (user.role() != Role.ADMIN && isOnlyAdminPath(req) && !req.getMethod().equals("GET")) {
            throw new ApplicationException("You do not have access rights to this resource", SC_FORBIDDEN);
        }

        chain.doFilter(req, res);
    }

    private boolean isFreePath(HttpServletRequest req) {
        var path = getRequestPath(req);

        return path.equals("/signin") || path.equals("/signup") || path.contains("/webjars") || path.equals("/swagger");
    }

    private boolean isOnlyUserPath(HttpServletRequest request) {
        var path = getRequestPath(request);

        return getRequestPath(request).equals("/api/v1/ticket/actual")
                || path.equals("/api/v1/passenger");//todo разобраться с методами POST
    }

    private boolean isOnlyAdminPath(HttpServletRequest request) {
        var path = getRequestPath(request);
        return path.equals("/api/v1/airport") || path.startsWith("/admin");
    }

    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI()
                .substring(request.getContextPath()
                        .length());
    }
}
