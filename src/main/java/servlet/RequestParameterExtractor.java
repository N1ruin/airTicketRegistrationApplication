package servlet;

import exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

public class RequestParameterExtractor {
    public Long extractId(HttpServletRequest req) {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid id parameter format " + idParam);
        }
    }

    public Long extractUserId(HttpServletRequest req) {
        String userIdParam = req.getParameter("userId");

        if (userIdParam == null || userIdParam.isBlank()) {
            return null;
        }

        try {
            return Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid user id parameter format " + userIdParam);
        }
    }
}
