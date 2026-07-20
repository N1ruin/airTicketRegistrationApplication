package servlet;

import converter.user.UserDtoConverter;
import converter.user.UserSignUpRequestConverter;
import domain.Role;
import dto.user.UserDto;
import dto.user.UserSignUpRequest;
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
import validation.service.ValidationService;

import java.io.IOException;

import static constant.ServletContextAttributeKey.*;

@Path("/ticket-app/signup")
public class SignUpServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(SignUpServlet.class);

    private UserService userService;
    private JsonHelper jsonHelper;
    private UserSignUpRequestConverter userSignUpRequestConverter;
    private UserDtoConverter userDtoConverter;
    private ValidationService validationService;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        userSignUpRequestConverter = (UserSignUpRequestConverter) context.getAttribute(USER_SIGN_UP_REQUEST_CONVERTER);
        userDtoConverter = (UserDtoConverter) context.getAttribute(USER_DTO_CONVERTER);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @Operation(tags = {"Users"}, summary = "Регистрация нового пользователя",
            description = "Создает пользователя и возвращает его ID с установленной ролью пользователя",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserSignUpRequest.class)),
                    required = true),
            responses = {@ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")})
    @POST
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, UserSignUpRequest.class);

        validationService.validate(request);

        var user = userSignUpRequestConverter.convert(request);

        var savedUser = userService.signUp(user, Role.USER);

        var dto = userDtoConverter.convert(savedUser);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), dto);
    }
}
