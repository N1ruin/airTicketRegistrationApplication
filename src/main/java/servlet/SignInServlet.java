package servlet;

import dto.user.UserAuthenticationRequest;
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

import static constant.AttributeName.HTTP_HELPER;
import static constant.AttributeName.USER_SERVICE;

@WebServlet("/signin")
public class SignInServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(SignInServlet.class);
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
        var authenticationRequest = httpHelper.getRequestBody(req, UserAuthenticationRequest.class);

        var userDto = userService.signIn(authenticationRequest.email(), authenticationRequest.password());

        var session = req.getSession();
        session.setAttribute("user", userDto);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDto);
    }
}
