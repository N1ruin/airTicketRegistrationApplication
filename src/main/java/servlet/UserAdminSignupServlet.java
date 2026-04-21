package servlet;

import converter.user.UserSignUpRequestConverter;
import converter.user.UserSignUpResponseConverter;
import dto.user.UserSignUpRequest;
import dto.user.UserSignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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
import validation.UserValidationService;

import java.io.IOException;

import static constant.AttributeName.*;
import static constant.AttributeName.USER_SIGN_UP_RESPONSE_CONVERTER;
import static constant.AttributeName.USER_VALIDATION_SERVICE;

@WebServlet("/admin/signup")
@Path("/ticket-app/admin/signup")
public class UserAdminSignupServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UserAdminSignupServlet.class);
    private UserService userService;
    private HttpHelper httpHelper;
    private UserSignUpRequestConverter userSignUpRequestConverter;
    private UserSignUpResponseConverter userSignUpResponseConverter;
    private UserValidationService userValidationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        userSignUpRequestConverter = (UserSignUpRequestConverter) context.getAttribute(USER_SIGN_UP_REQUEST_CONVERTER);
        userSignUpResponseConverter = (UserSignUpResponseConverter) context.getAttribute(USER_SIGN_UP_RESPONSE_CONVERTER);
        userValidationService = (UserValidationService) context.getAttribute(USER_VALIDATION_SERVICE);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Operation(tags = {"Users"}, summary = "Регистрация нового администратора",
            description = "Создает пользователя и возвращает его ID с установленной ролью администратора",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserSignUpRequest.class)),
                    required = true),
            responses = {@ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserSignUpResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")})
    @POST
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var request = httpHelper.getRequestBody(req, UserSignUpRequest.class);

        userValidationService.validateSignUpRequest(request);

        var user = userSignUpRequestConverter.convert(request);

        var savedUser = userService.signUpAdmin(user);

        var response = userSignUpResponseConverter.convert(savedUser);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, response);
    }
}
