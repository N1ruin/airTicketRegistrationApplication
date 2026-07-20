package servlet;

import converter.user.UserDtoConverter;
import dto.user.UserAuthenticationRequest;
import exception.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.UserService;
import util.JsonHelper;

import java.io.IOException;

import static constant.ServletContextAttributeKey.*;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;

@Path("/ticket-app/signin")
public class SignInServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(SignInServlet.class);

    private UserService userService;
    private JsonHelper jsonHelper;
    private UserDtoConverter userDtoConverter;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();
        userService = (UserService) context.getAttribute(USER_SERVICE);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        userDtoConverter = (UserDtoConverter) context.getAttribute(USER_DTO_CONVERTER);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
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
        var session = req.getSession(false);
        if (session != null) {
            throw new ApplicationException("User already authenticated", SC_CONFLICT);
        }

        var body = new String(req.getInputStream().readAllBytes());
        var authenticationRequest = jsonHelper.fromJson(body, UserAuthenticationRequest.class);

        var loggedUser = userService.signIn(authenticationRequest.email(), authenticationRequest.password());

        var userDto = userDtoConverter.convert(loggedUser);

        var newSession = req.getSession();
        newSession.setAttribute("user", userDto);
    }
}
