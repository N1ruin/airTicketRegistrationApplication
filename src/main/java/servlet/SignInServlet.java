package servlet;

import dto.user.UserAuthenticationRequest;
import dto.user.UserDto;
import exception.UserAlreadyAuthenticatedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.UserService;

import java.io.IOException;

import static constant.AttributeName.HTTP_HELPER;
import static constant.AttributeName.USER_SERVICE;

@WebServlet("/signin")
@Path("/ticket-app/signin")
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

    @Operation(tags = {"Users"}, summary = "Вход в систему",
            description = "Вход систему по предоставленным логину и паролю. Создает JSESSIONID",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserAuthenticationRequest.class)),
                    required = true),
            responses = {@ApiResponse(responseCode = "401", description = "Неверный логин или пароль")})
    @POST
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var user = (UserDto) req.getSession().getAttribute("user");
        if (user != null) {
            throw new UserAlreadyAuthenticatedException();
        }

        var authenticationRequest = httpHelper.getRequestBody(req, UserAuthenticationRequest.class);

        var userDto = userService.signIn(authenticationRequest.email(), authenticationRequest.password());

        var session = req.getSession();
        session.setAttribute("user", userDto);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDto);
    }
}
