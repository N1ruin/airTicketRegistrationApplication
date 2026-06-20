package servlet;

import converter.flight.*;
import dto.error.ErrorDto;
import dto.flight.CreateFlightRequest;
import dto.flight.UpdateFlightRequest;
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
import jakarta.ws.rs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.FlightService;
import util.CurrentUserHolder;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.RequestParameterValidationService;
import validation.validator.ValidationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/flight")
@Path("/ticket-app/api/v1/flight")
public class FlightServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(FlightServlet.class);
    private FlightService flightService;
    private RequestParameterExtractor parameterExtractor;
    private CreateFlightRequestConverter createFlightRequestConverter;
    private FlightConverter flightConverter;
    private UpdateFlightRequestConverter flightRequestConverter;
    private RequestParameterValidationService requestParameterValidationService;
    private JsonHelper jsonHelper;
    private ValidationService validationService;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        flightService = (FlightService) context.getAttribute(FLIGHT_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        createFlightRequestConverter =
                (CreateFlightRequestConverter) context.getAttribute(CREATE_FLIGHT_REQUEST_CONVERTER);
        flightConverter = (FlightConverter) context.getAttribute(FLIGHT_CONVERTER);
        flightRequestConverter =
                (UpdateFlightRequestConverter) context.getAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @GET
    @Operation(tags = {"Flights"}, summary = "Получение рейса или списка рейсов",
            description = "Если id не передан, возвращает все рейсы",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id рейса", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
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

    @POST
    @Operation(tags = {"Flights"}, summary = "Создание рейса",
            description = "Создание рейса",
            requestBody = @RequestBody(description = "Данные рейса, аэропортов отправления и прибытия", required = true,
                    content = @Content(schema = @Schema(implementation = CreateFlightRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, CreateFlightRequest.class);

        validationService.validate(request);

        var flight = createFlightRequestConverter.convert(request);

        var savedFlight = flightService.create(flight);

        var dto = flightConverter.convert(savedFlight);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @PUT
    @Operation(tags = {"Flights"}, summary = "Обновление данных рейса по id",
            description = "Обновление рейса по id",
            requestBody = @RequestBody(description = "Обновленные данные рейса", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateFlightRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var updateFlightRequest = jsonHelper.fromJson(body, UpdateFlightRequest.class);

        validationService.validate(updateFlightRequest);

        var flight = flightRequestConverter.convert(updateFlightRequest);

        var updatedFlight = flightService.update(flight);

        var flightDto = flightConverter.convert(updatedFlight);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(flightDto).getBytes(StandardCharsets.UTF_8));
    }

    @DELETE
    @Operation(tags = {"Flights"}, summary = "Удаление рейса по id",
            description = "Удаление рейса по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id рейса", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "204", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req,
                         @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req, true);

        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            var errorDto = new ErrorDto(HttpServletResponse.SC_BAD_REQUEST,
                    "Parameter 'id' is required for deletion",
                    ZonedDateTime.now());
            resp.getOutputStream().write(jsonHelper.toJson(errorDto).getBytes(StandardCharsets.UTF_8));

            return;
        }

        requestParameterValidationService.validateId(id);

        flightService.delete(id);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private String findAll() {
        var flights = flightService.findAll();

        var flightDtos = flightConverter.convertAll(flights);

        return jsonHelper.toJson(flightDtos);
    }

    private String findById(Long id) {
        requestParameterValidationService.validateId(id);

        var flight = flightService.findById(id);

        var flightDto = flightConverter.convert(flight);

        return jsonHelper.toJson(flightDto);
    }
}
