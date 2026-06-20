package servlet;

import dto.passenger.UpdatePassengerRequest;
import dto.passport.PassportDto;
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
import service.PassengerService;
import util.CurrentUserHolder;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.service.RequestParameterValidationService;
import validation.service.ValidationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/passenger")
@Path("/ticket-app/api/v1/passenger")
public class PassengerServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(PassengerServlet.class);
    private JsonHelper jsonHelper;
    private PassengerService passengerService;
    private RequestParameterExtractor parameterExtractor;
    private ValidationService validationService;
    private PassportDtoToPassengerConverter passportDtoToPassengerConverter;
    private PassengerDtoConverter passengerDtoConverter;
    private UpdatePassengerRequestConverter updatePassengerRequestConverter;
    private RequestParameterValidationService requestParameterValidationService;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        passengerService = (PassengerService) context.getAttribute(PASSENGER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        validationService = (ValidationService) context.getAttribute(PASSENGER_VALIDATION_SERVICE);
        passportDtoToPassengerConverter =
                (PassportDtoToPassengerConverter) context.getAttribute(CREATE_PASSENGER_REQUEST_CONVERTER);

        passengerDtoConverter = (PassengerDtoConverter) context.getAttribute(PASSENGER_DTO_CONVERTER);
        updatePassengerRequestConverter =
                (UpdatePassengerRequestConverter) context.getAttribute(UPDATE_PASSENGER_REQUEST_CONVERTER);

        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @POST
    @Operation(tags = {"Passengers"}, summary = "Создание пассажира",
            description = "Создание пассажира",
            requestBody = @RequestBody(description = "Данные пассажира", required = true,
                    content = @Content(schema = @Schema(implementation = PassportDto.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден"),
                    @ApiResponse(responseCode = "409", description = "Пассажир с таким паспортом уже существует")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var passportDto = jsonHelper.fromJson(body, PassportDto.class);

        validationService.validate(passportDto);

        var passenger = passportDtoToPassengerConverter.convert(passportDto);

        var savedPassenger = passengerService.create(passenger);

        var createPassengerResponse = passengerDtoConverter.convert(savedPassenger);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(createPassengerResponse).getBytes(StandardCharsets.UTF_8));
    }

    @GET
    @Operation(tags = {"Passengers"}, summary = "Получение пассажира или списка пассажиров",
            description = "Если id не передан, возвращает всех пассажиров",
            parameters = {@Parameter(name = "passengerId", in = ParameterIn.QUERY, description = "Id пассажира", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var passengerId = parameterExtractor.extractId(req, false);
        var currentUserId = CurrentUserHolder.getCurrentUserId();
        boolean isAdmin = CurrentUserHolder.isAdmin();

        String responseBodyJson;
        if (passengerId != null) {
            var passenger = isAdmin
                    ? passengerService.findById(passengerId)
                    : passengerService.findByIdAndUserId(passengerId, currentUserId);
            responseBodyJson = jsonHelper.toJson(passengerDtoConverter.convert(passenger));
        } else {
            var passengers = isAdmin
                    ? passengerService.findAll()
                    : passengerService.findAllByUserId(currentUserId);
            responseBodyJson = jsonHelper.toJson(passengerDtoConverter.convertAll(passengers));
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
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
        if (CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, UpdatePassengerRequest.class);

        validationService.validate(request);

        var passenger = updatePassengerRequestConverter.convert(request);

        var updatedPassenger = passengerService.update(passenger);

        var dto = passengerDtoConverter.convert(updatedPassenger);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @DELETE
    @Operation(tags = {"Passengers"}, summary = "Удаление пассажира по id",
            description = "Удаление пассажира по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id пассажира", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "204", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пассажир не найден")})
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req,
                         @Parameter(hidden = true) HttpServletResponse resp) {
        if (CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var passengerId = parameterExtractor.extractId(req, true);

        requestParameterValidationService.validateId(passengerId);

        passengerService.delete(passengerId);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
