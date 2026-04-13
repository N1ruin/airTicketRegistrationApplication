package filter;

import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import service.HttpHelper;

import java.io.IOException;

import static constant.AttributeName.HTTP_HELPER;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class GlobalExceptionHandlerFilter extends HttpFilter {
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
        } catch (Exception e) {
            handleException(res, SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    private void handleException(ServletResponse res, int status, Throwable throwable) throws IOException {
        var response = (HttpServletResponse) res;
        response.setStatus(status);
        httpHelper.writeResponseBody(response, throwable.getMessage());
    }
}
