package servlet;

import dto.airport.CreateAirportRequest;
import dto.airport.UpdateAirportRequest;
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
import converter.airport.*;
import jakarta.ws.rs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AirportService;
import util.CurrentUserHolder;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.validator.ValidationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/airport")
@Path("/ticket-app/api/v1/airport")
public class AirportServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(AirportServlet.class);
    private AirportService airportService;
    private CreateAirportRequestConverter createAirportRequestConverter;
    private UpdateAirportRequestConverter updateAirportRequestConverter;
    private AirportConverter airportConverter;
    private ValidationService validationService;
    private JsonHelper jsonHelper;
    private RequestParameterExtractor parameterExtractor;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();
        airportService = (AirportService) context.getAttribute(AIRPORT_SERVICE);
        createAirportRequestConverter =
                (CreateAirportRequestConverter) context.getAttribute(CREATE_AIRPORT_REQUEST_CONVERTER);
        updateAirportRequestConverter =
                (UpdateAirportRequestConverter) context.getAttribute(UPDATE_AIRPORT_REQUEST_CONVERTER);
        airportConverter = (AirportConverter) context.getAttribute(AIRPORT_CONVERTER);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @POST
    @Operation(tags = {"Airports"}, summary = "Создание аэропорта",
            description = "Создание аэропорта",
            requestBody = @RequestBody(description = "Данные аэропорта и адреса", required = true,
                    content = @Content(schema = @Schema(implementation = CreateAirportRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Аэропорт не найден")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, CreateAirportRequest.class);

        validationService.validate(request);

        var airport = createAirportRequestConverter.convert(request);

        var createdAirport = airportService.create(airport);

        var dto = airportConverter.convert(createdAirport);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(dto).getBytes());
    }

    @GET
    @Operation(tags = {"Airports"}, summary = "Получение аэропорта или списка аэропортов",
            description = "Если id не передан, возвращает все аэропорты",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id аэропорта", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Аэропорт не найден")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var code = parameterExtractor.extractAirportCode(req, false);

        String responseBodyJson;
        if (code == null) {
            responseBodyJson = findAll();
        } else {
            responseBodyJson = findByCode(code);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
    }

    @PUT
    @Operation(tags = {"Airports"}, summary = "Обновление данных аэропорта по коду",
            description = "Обновление аэропорта по коду",
            requestBody = @RequestBody(description = "Обновленные данные аэропорта", required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAirportRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Аэропорт не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, UpdateAirportRequest.class);

        validationService.validate(request);

        var airport = updateAirportRequestConverter.convert(request);

        var updatedAirport = airportService.update(airport);

        var dto = airportConverter.convert(updatedAirport);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(dto).getBytes());
    }

    private String findAll() {
        var airports = airportService.findAll();
        var airportDtos = airportConverter.convertAll(airports);

        return jsonHelper.toJson(airportDtos);
    }

    private String findByCode(String code) {

        var airport = airportService.findById(code);

        var dto = airportConverter.convert(airport);

        return jsonHelper.toJson(dto);
    }
}
