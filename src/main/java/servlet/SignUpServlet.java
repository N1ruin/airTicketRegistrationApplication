package servlet;

import dto.user.UserSignUpRequest;
import exception.UserAlreadyExistException;
import exception.ValidationException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.UserService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/sign-up")
public class SignUpServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(SignUpServlet.class);

    private UserService userService;
    private HttpHelper httpHelper;


    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            var request = httpHelper.getRequestBody(req, UserSignUpRequest.class);
            var savedUserData = userService.signUp(request);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            httpHelper.writeResponseBody(resp, savedUserData);
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpHelper.writeResponseBody(resp, e.getValidationErrorMessages());
        } catch (UserAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpHelper.writeResponseBody(resp, e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpHelper.writeResponseBody(resp, e.getMessage());
        }
    }
}
