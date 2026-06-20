package util;

import exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

public class RequestParameterExtractor {
    public Long extractId(HttpServletRequest req, boolean required) {
        return extractLong(req, "id", required);
    }

    public String extractAirportCode(HttpServletRequest req, boolean required) {
        return extractString(req, "airport-code", required);
    }

    private Long extractLong(HttpServletRequest req, String paramName, boolean required) {
        String param = req.getParameter(paramName);

        if (param == null || param.isBlank()) {
            if (required) {
                throw new ValidationException("Parameter " + paramName + " is required");
            }
            return null;
        }

        try {
            return Long.parseLong(param);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + paramName + " format: " + param);
        }
    }

    private String extractString(HttpServletRequest req, String paramName, boolean required) {
        String param = req.getParameter(paramName);

        if (param == null || param.isBlank()) {
            if (required) {
                throw new ValidationException("Parameter " + paramName + " is required");
            }
            return null;
        }

        return param;
    }
}
