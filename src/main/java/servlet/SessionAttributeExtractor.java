package servlet;

import domain.Role;
import dto.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;

public class SessionAttributeExtractor {
    public Role extractRole(HttpServletRequest request) {
        var user = (UserDto) request.getSession().getAttribute("user");

        return user.role();
    }

    public Long extractId(HttpServletRequest request) {
        var user = (UserDto) request.getSession().getAttribute("user");

        return user.id();
    }
}
