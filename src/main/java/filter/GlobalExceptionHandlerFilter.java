package filter;

import exception.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;

import java.io.IOException;

import static constant.AttributeName.HTTP_HELPER;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class GlobalExceptionHandlerFilter extends HttpFilter {
    private static final Logger log = LogManager.getLogger(GlobalExceptionHandlerFilter.class);
    private HttpHelper httpHelper;

    @Override
    public void init(FilterConfig config) {
        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, res);
        } catch (ValidationException | IOException e) {
            handleException(res, SC_BAD_REQUEST, e);
        } catch (EntityNotFoundException e) {
            handleException(res, SC_NOT_FOUND, e);
        } catch (EntityAlreadyExistException e) {
            handleException(res, SC_CONFLICT, e);
        } catch (InvalidCredentialsException | DontHavePermissionException e) {
            handleException(res, SC_UNAUTHORIZED, e);
        } catch (UserAlreadyAuthenticatedException e) {
            handleException(res, SC_FORBIDDEN, e);
        } catch (Exception e) {
            handleException(res, SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    private void handleException(ServletResponse res, int status, Throwable throwable) throws IOException {
        log.error("Unhandled exception caught in filter: ", throwable);

        var response = (HttpServletResponse) res;
        response.setStatus(status);

        String errorMessage = throwable.getMessage();
        if (errorMessage == null) {
            errorMessage = "Internal Error: " + throwable.getClass().getName() +
                    " (check server logs for details)";
        }

        httpHelper.writeResponseBody(response, errorMessage);
    }
}
