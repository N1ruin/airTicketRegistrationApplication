package util;

import exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.function.Function;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

public class RequestParameterExtractor {
    public <T> T extractParameter(HttpServletRequest req, String paramName,
                                  boolean required, Function<String, T> converter) {
        var param = req.getParameter(paramName);

        if (param == null || param.isBlank()) {
            if (required) {
                throw new ApplicationException("Parameter " + paramName + " is required", SC_BAD_REQUEST);
            }
            return null;
        }

        try {
            return converter.apply(param);
        } catch (Exception e) {
            throw new ApplicationException("Invalid " + paramName + " format: " + param, SC_BAD_REQUEST);
        }
    }
}
