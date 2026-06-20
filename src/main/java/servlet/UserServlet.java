package servlet;

import converter.user.UserDtoConverter;
import dto.user.UpdateUserRequest;
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
import service.UserService;
import util.CurrentUserHolder;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.service.RequestParameterValidationService;
import validation.service.ValidationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/user")
@Path("/ticket-app/api/v1/user")
public class UserServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UserServlet.class);
    private JsonHelper jsonHelper;
    private UserService userService;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;
    private UserDtoConverter userDtoConverter;
    private ValidationService validationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        userService = (UserService) context.getAttribute(USER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        userDtoConverter = (UserDtoConverter) context.getAttribute(USER_DTO_CONVERTER);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
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
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req, false);

        String responseBodyJson;
        if (id == null) {
            responseBodyJson = findAll();
        } else {
            responseBodyJson = findById(id);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
    }

    @PUT
    @Operation(tags = {"Users"}, summary = "Обновление данных пользователя",
            description = "Позволяет пользователю изменить свои данные. Только для пользователей",
            requestBody = @RequestBody(description = "Обновленные данные пользователя", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Данные успешно обновлены",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, UpdateUserRequest.class);

        validationService.validate(request);

        var updatedUser = userService.update(request.newPassword());

        var response = userDtoConverter.convert(updatedUser);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(response).getBytes(StandardCharsets.UTF_8));
    }

    private String findById(long id) {
        requestParameterValidationService.validateId(id);

        var user = userService.findById(id);

        var userDto = userDtoConverter.convert(user);

        return jsonHelper.toJson(userDto);
    }

    private String findAll() {
        var passengers = userService.findAll();

        var userDtos = userDtoConverter.convertAll(passengers);

        return jsonHelper.toJson(userDtos);
    }
}
