package servlet;

import converter.airport.AirportConverter;
import converter.airport.AirportDtoConverter;
import converter.airport.UpdateAirportRequestConverter;
import dto.airport.AirportDto;
import dto.airport.UpdateAirportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AirportService;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.service.ValidationService;

import java.io.IOException;
import java.util.function.Function;

import static constant.ServletContextAttributeKey.*;

@Path("/ticket-app/api/v1/airport")
public class AirportServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(AirportServlet.class);

    private AirportService airportService;
    private UpdateAirportRequestConverter updateAirportRequestConverter;
    private AirportConverter airportConverter;
    private AirportDtoConverter airportDtoConverter;
    private ValidationService validationService;
    private JsonHelper jsonHelper;
    private RequestParameterExtractor parameterExtractor;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();
        airportService = (AirportService) context.getAttribute(AIRPORT_SERVICE);
        updateAirportRequestConverter =
                (UpdateAirportRequestConverter) context.getAttribute(UPDATE_AIRPORT_REQUEST_CONVERTER);
        airportConverter = (AirportConverter) context.getAttribute(AIRPORT_CONVERTER);
        airportDtoConverter = (AirportDtoConverter) context.getAttribute(AIRPORT_DTO_CONVERTER);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @POST
    @Operation(tags = {"Airports"}, summary = "Создание аэропорта",
            description = "Создание аэропорта",
            requestBody = @RequestBody(description = "Данные аэропорта и адреса", required = true,
                    content = @Content(schema = @Schema(implementation = AirportDto.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Аэропорт не найден")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, AirportDto.class);

        validationService.validate(request);

        var airport = airportDtoConverter.convert(request);

        var createdAirport = airportService.create(airport);

        var dto = airportConverter.convert(createdAirport);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), dto);
    }

    @GET
    @Operation(tags = {"Airports"}, summary = "Получение аэропорта",
            description = "Возвращает аэропорт по переданному id",
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
        var code = parameterExtractor.extractParameter(req, "id", false, Function.identity());

        var airport = airportService.findById(code);

        var dto = airportConverter.convert(airport);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), dto);
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
        var body = new String(req.getInputStream().readAllBytes());

        var request = jsonHelper.fromJson(body, UpdateAirportRequest.class);

        validationService.validate(request);

        var airport = updateAirportRequestConverter.convert(request);

        var updatedAirport = airportService.update(airport);

        var dto = airportConverter.convert(updatedAirport);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), dto);
    }
}
