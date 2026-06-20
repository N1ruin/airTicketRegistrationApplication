package listener;

import dto.user.UserDto;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import util.CurrentUserHolder;

public class CurrentUserHolderContextListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        var request = (HttpServletRequest) sre.getServletRequest();
        var user = (UserDto) request.getSession().getAttribute("user");

        if (user != null) {
            CurrentUserHolder.setCurrentUserId(user.id());
            CurrentUserHolder.setCurrentUserRole(user.role());
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        CurrentUserHolder.clear();
    }
}
