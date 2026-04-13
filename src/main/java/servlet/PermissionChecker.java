package servlet;

import domain.Role;
import jakarta.servlet.http.HttpServletRequest;

public class PermissionChecker {
    private final SessionAttributeExtractor roleExtractor;

    public PermissionChecker(SessionAttributeExtractor roleExtractor) {
        this.roleExtractor = roleExtractor;
    }

    public boolean isAdmin(HttpServletRequest request) {
        return roleExtractor.extractRole(request) == Role.ADMIN;
    }
}
