package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class HttpHelper {
    private final ObjectMapper objectMapper;
    private static final Logger log = LogManager.getLogger(HttpHelper.class);

    public HttpHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T getRequestBody(HttpServletRequest request, Class<T> targetClass) throws IOException {
        try {
            return objectMapper.readValue(request.getInputStream(), targetClass);
        } catch (IOException e) {
            log.error("Deserialization request body error", e);
            throw e;
        }
    }

    public <T> void writeResponseBody(HttpServletResponse response, T object) {
        try {
            response.setContentType("application/json");
            response.getOutputStream().write(objectMapper.writeValueAsBytes(object));
        } catch (Exception e) {
            log.error("Error write response body", e);
        }
    }
}
