package servlet;

import dto.passenger.CreatePassengerRequest;
import dto.passenger.UpdatePassengerRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import converter.passenger.*;
import jakarta.ws.rs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.PassengerService;
import validation.PassengerValidationService;
import validation.RequestParameterValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/passenger")
@Path("/ticket-app/api/v1/passenger")
public class PassengerServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(PassengerServlet.class);
    private HttpHelper httpHelper;
    private PassengerService passengerService;
    private RequestParameterExtractor parameterExtractor;
    private PassengerValidationService passengerValidationService;
    private CreatePassengerRequestConverter createPassengerRequestConverter;
    private CreatePassengerResponseConverter createPassengerResponseConverter;
    private PassengerDtoConverter passengerDtoConverter;
    private UpdatePassengerRequestConverter updatePassengerRequestConverter;
    private UpdatePassengerResponseConverter updatePassengerResponseConverter;
    private RequestParameterValidationService requestParameterValidationService;
    private PermissionChecker permissionChecker;
    private SessionAttributeExtractor sessionAttributeExtractor;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        passengerService = (PassengerService) context.getAttribute(PASSENGER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        passengerValidationService = (PassengerValidationService) context.getAttribute(PASSENGER_VALIDATION_SERVICE);
        createPassengerRequestConverter =
                (CreatePassengerRequestConverter) context.getAttribute(CREATE_PASSENGER_REQUEST_CONVERTER);
        createPassengerResponseConverter =
                (CreatePassengerResponseConverter) context.getAttribute(CREATE_PASSENGER_RESPONSE_CONVERTER);
        passengerDtoConverter = (PassengerDtoConverter) context.getAttribute(PASSENGER_DTO_CONVERTER);
        updatePassengerRequestConverter =
                (UpdatePassengerRequestConverter) context.getAttribute(UPDATE_PASSENGER_REQUEST_CONVERTER);
        updatePassengerResponseConverter =
                (UpdatePassengerResponseConverter) context.getAttribute(UPDATE_PASSENGER_RESPONSE_CONVERTER);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @POST
    @Operation(tags = {"Passengers"}, summary = "Создание пассажира",
            description = "Создание пассажира",
            requestBody = @RequestBody(description = "Данные пассажира", required = true,
                    content = @Content(schema = @Schema(implementation = CreatePassengerRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден"),
                    @ApiResponse(responseCode = "409", description = "Пассажир с таким паспортом уже существует")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var request = httpHelper.getRequestBody(req, CreatePassengerRequest.class);

        passengerValidationService.validateCreateRequest(request);

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var passenger = createPassengerRequestConverter.convert(request);
        passenger.setUserId(currentUserId);

        var savedPassenger = passengerService.save(passenger);

        var createPassengerResponse = createPassengerResponseConverter.convert(savedPassenger);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, createPassengerResponse);
    }

    @GET
    @Operation(tags = {"Passengers"}, summary = "Получение пассажира или списка пассажиров",
            description = "Если id не передан, возвращает всех пассажиров",
            parameters = {@Parameter(name = "userId", in = ParameterIn.QUERY, description = "Id пользователя", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var userId = parameterExtractor.extractUserId(req);
        var currentUserId = sessionAttributeExtractor.extractId(req);
        boolean isAdmin = permissionChecker.isAdmin(req);

        boolean hasUserIdParam = req.getParameterMap().containsKey("userId");

        if (hasUserIdParam) {
            if (isAdmin || (userId != null && userId.equals(currentUserId))) {
                findAllByUserId(resp, userId);
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            if (isAdmin) {
                findAll(resp);
            } else {
                findAllByUserId(resp, currentUserId);
            }
        }
    }

    @PUT
    @Operation(tags = {"Passengers"}, summary = "Обновление данных пассажира по id",
            description = "Обновление пассажира по id",
            requestBody = @RequestBody(description = "Обновленные данные пассажира", required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePassengerRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пассажир не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var request = httpHelper.getRequestBody(req, UpdatePassengerRequest.class);

        passengerValidationService.validateUpdateRequest(request);

        var passenger = updatePassengerRequestConverter.convert(request);

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var updatedPassenger = passengerService.update(passenger, currentUserId);

        var dto = updatePassengerResponseConverter.convert(updatedPassenger);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dto);
    }

    @DELETE
    @Operation(tags = {"Passengers"}, summary = "Удаление пассажира по id",
            description = "Удаление пассажира по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id пассажира", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пассажир не найден")})
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req,
                         @Parameter(hidden = true) HttpServletResponse resp) {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var passengerId = parameterExtractor.extractId(req);

        requestParameterValidationService.validateId(passengerId);

        var userId = sessionAttributeExtractor.extractId(req);
        passengerService.delete(passengerId, userId);

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void findAllByUserId(HttpServletResponse resp, Long userId) throws IOException {
        requestParameterValidationService.validateId(userId);

        var passengers = passengerService.findAllByUserId(userId);

        var dtoResponse = passengerDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dtoResponse);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var passengers = passengerService.findAll();

        var passengerDtos = passengerDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, passengerDtos);
    }
}
