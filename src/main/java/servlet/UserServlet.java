package servlet;

import converter.user.UserDtoConverter;
import converter.user.UserUpdateResponseConverter;
import dto.user.UpdateUserRequest;
import dto.user.UpdateUserResponse;
import dto.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.UserService;
import validation.RequestParameterValidationService;
import validation.UserValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/user")
@Path("/ticket-app/api/v1/user")
public class UserServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UserServlet.class);
    private HttpHelper httpHelper;
    private UserService userService;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;
    private UserDtoConverter userDtoConverter;
    private PermissionChecker permissionChecker;
    private UserValidationService userValidationService;
    private UserUpdateResponseConverter userUpdateResponseConverter;
    private SessionAttributeExtractor sessionAttributeExtractor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        userService = (UserService) context.getAttribute(USER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        userDtoConverter = (UserDtoConverter) context.getAttribute(USER_DTO_CONVERTER);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        userValidationService = (UserValidationService) context.getAttribute(USER_VALIDATION_SERVICE);
        userUpdateResponseConverter = (UserUpdateResponseConverter) context.getAttribute(USER_UPDATE_RESPONSE_CONVERTER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @GET
    @Operation(tags = {"Users"},
            summary = "Получение пользователя или списка пользователей",
            description = "Если id не передан, возвращает всех",
            parameters = {
                    @Parameter(name = "id",
                            in = ParameterIn.QUERY,
                            description = "ID пользователя",
                            example = "1",
                            schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех",
                    content = @Content(schema = @Schema(anyOf = {UserDto.class, UserDto[].class}))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")}
    )
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req);

        if (id == null) {
            findAll(resp);
        } else {
            findById(resp, id);
        }
    }

    @PUT
    @Operation(tags = {"Users"}, summary = "Обновление данных пользователя",
            description = "Позволяет пользователю изменить свои данные. Только для пользователей",
            requestBody = @RequestBody(description = "Обновленные данные пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Данные успешно обновлены",
                    content = @Content(schema = @Schema(implementation = UpdateUserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var currentUserId = sessionAttributeExtractor.extractId(req);

        var request = httpHelper.getRequestBody(req, UpdateUserRequest.class);

        userValidationService.validateUpdateRequest(request);

        var updatedUser = userService.update(request.id(), request.newPassword(), request.firstName(), request.lastName(),
                request.fatherName(), currentUserId);

        var response = userUpdateResponseConverter.convert(updatedUser);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, response);
    }

    private void findById(HttpServletResponse resp, long id) throws IOException {
        requestParameterValidationService.validateId(id);

        var user = userService.findById(id);

        var userDto = userDtoConverter.convert(user);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDto);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var passengers = userService.findAll();

        var userDtos = userDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDtos);
    }
}
