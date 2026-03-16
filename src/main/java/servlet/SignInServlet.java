package servlet;

import dto.user.UserAuthenticationRequest;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HttpHelper;
import service.UserService;

import java.io.IOException;

import static constant.AttributeName.HTTP_HELPER;
import static constant.AttributeName.USER_SERVICE;

@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {
    private UserService userService;
    private HttpHelper httpHelper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //log
        var context = config.getServletContext();
        userService = (UserService) context.getAttribute(USER_SERVICE);
        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        //log
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var authenticationRequest = httpHelper.getRequestBody(req, UserAuthenticationRequest.class);

            var authResponse = userService.signIn(authenticationRequest);

            if (authResponse != null) {
                var session = req.getSession();
                session.setAttribute("user", authResponse);
                resp.setStatus(HttpServletResponse.SC_OK);
                httpHelper.writeResponseBody(resp, authResponse);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpHelper.writeResponseBody(resp, "");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpHelper.writeResponseBody(resp, "Invalid request body");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpHelper.writeResponseBody(resp, e.getMessage());
        }
    }
}
