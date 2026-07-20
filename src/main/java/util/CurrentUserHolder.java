package util;

import domain.Role;

public final class CurrentUserHolder {
    private static final ThreadLocal<Long> currentUserIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<Role> currentUserRoleHolder = new ThreadLocal<>();

    private CurrentUserHolder() {
    }

    public static void setCurrentUserId(Long userId) {
        currentUserIdHolder.set(userId);
    }

    public static Long getCurrentUserId() {
        return currentUserIdHolder.get();
    }

    public static void setCurrentUserRole(Role role) {
        currentUserRoleHolder.set(role);
    }

    public static Role getCurrentUserRole() {
        return currentUserRoleHolder.get();
    }

    public static boolean isAdmin() {
        return Role.ADMIN.equals(getCurrentUserRole());
    }

    public static void clear() {
        currentUserIdHolder.remove();
        currentUserRoleHolder.remove();
    }
}
