package filter;

import dto.error.ErrorDto;
import exception.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import static constant.ServletContextAttributeKey.JSON_HELPER;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
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
        log.error("Unhandled exception caught in filter: ", throwable);

        var response = (HttpServletResponse) res;
        response.setStatus(status);

        if (status >= 500) {
            log.error("Internal server error: ", throwable);
        } else {
            log.warn("Client error. Status: {}, message: {}", status, throwable.getMessage());
        }

        var errorMessage = throwable.getMessage();
        if (errorMessage == null) {
            errorMessage = status >= 500 ? "Internal server error" : "Error occurred";
        }

        if (throwable instanceof RepositoryException && throwable.getCause() != null) {
            log.debug("SQL error cause: {}", throwable.getCause().getMessage());
        }

        res.setContentType("application/json");
        var errorDto = new ErrorDto(status, errorMessage, ZonedDateTime.now());
        res.getOutputStream().write(jsonHelper.toJson(errorDto).getBytes(StandardCharsets.UTF_8));
    }

    private int determineStatus(Exception e) {
        return switch (e) {
            case ValidationException ignored -> SC_BAD_REQUEST;
            case IOException ignored -> SC_BAD_REQUEST;
            case EntityNotFoundException ignored -> SC_NOT_FOUND;
            case EntityAlreadyExistException ignored -> SC_CONFLICT;
            case InvalidCredentialsException ignored -> SC_UNAUTHORIZED;
            case AccessDeniedException ignored -> SC_FORBIDDEN;
            case UserAlreadyAuthenticatedException ignored -> SC_FORBIDDEN;
            default -> SC_INTERNAL_SERVER_ERROR;
        };
    }
}
