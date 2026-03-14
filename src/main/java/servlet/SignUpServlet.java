package servlet;

import dto.user.UserSignUpRequest;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapper.user.UserSignUpMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.UserService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet(urlPatterns = "/sign-up")
public class SignUpServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(SignUpServlet.class);

    private UserService userService;
    private HttpHelper httpHelper;


    @Override
    public void init(ServletConfig config) {
        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            var request = httpHelper.getRequestBody(req, UserSignUpRequest.class);
            var savedUser = userService.sigUp(request);
            httpHelper.writeResponseBody(resp, savedUser);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
