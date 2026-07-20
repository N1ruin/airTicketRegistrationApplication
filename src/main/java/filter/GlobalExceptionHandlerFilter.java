package filter;

import dto.error.ErrorDto;
import exception.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonHelper;

import java.io.IOException;
import java.time.ZonedDateTime;

import static constant.ServletContextAttributeKey.JSON_HELPER;
import static jakarta.servlet.http.HttpServletResponse.*;

public class GlobalExceptionHandlerFilter extends HttpFilter {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandlerFilter.class);
    private JsonHelper jsonHelper;

    @Override
    public void init(FilterConfig config) {
        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            int status = determineStatus(e);
            handleException(res, status, e);
        }
    }

    private void handleException(ServletResponse res, int status, Throwable throwable) throws IOException {
        var response = (HttpServletResponse) res;
        response.setStatus(status);

        var errorMessage = throwable.getMessage();
        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = status >= 500 ? "Internal server error" : "Error occurred";
        }

        if (status >= 500) {
            log.error("Internal server error {}: {}", status, errorMessage, throwable);
        } else {
            log.info("Client error. Status: {}, message: {}", status, throwable.getMessage());
            if (throwable.getCause() != null) {
                log.info("Error cause: {}", throwable.getCause().getMessage());
            }
        }

        res.setContentType("application/json");
        var errorDto = new ErrorDto(status, errorMessage, ZonedDateTime.now());
        jsonHelper.writeBytes(res.getOutputStream(), errorDto);
    }

    private int determineStatus(Exception e) {
        if (e instanceof ApplicationException exception) {
            return exception.getErrorCode();
        } else {
            return SC_INTERNAL_SERVER_ERROR;
        }
    }
}
