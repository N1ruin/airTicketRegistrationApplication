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
import service.HttpHelper;
import validation.AirportValidationService;
import validation.RequestParameterValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/airport")
@Path("/ticket-app/api/v1/airport")
public class AirportServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(AirportServlet.class);
    private HttpHelper httpHelper;
    private AirportService airportService;
    private AirportValidationService airportValidationService;
    private CreateAirportRequestConverter createAirportRequestConverter;
    private CreateAirportResponseConverter createAirportResponseConverter;
    private UpdateAirportRequestConverter updateAirportRequestConverter;
    private UpdateAirportResponseConverter updateAirportResponseConverter;
    private AirportConverter airportConverter;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;
    private PermissionChecker permissionChecker;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();
        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        airportService = (AirportService) context.getAttribute(AIRPORT_SERVICE);
        airportValidationService = (AirportValidationService) context.getAttribute(AIRPORT_VALIDATION_SERVICE);
        createAirportRequestConverter = (CreateAirportRequestConverter) context.getAttribute(CREATE_AIRPORT_REQUEST_CONVERTER);
        createAirportResponseConverter = (CreateAirportResponseConverter) context.getAttribute(CREATE_AIRPORT_RESPONSE_CONVERTER);
        updateAirportRequestConverter = (UpdateAirportRequestConverter) context.getAttribute(UPDATE_AIRPORT_REQUEST_CONVERTER);
        updateAirportResponseConverter = (UpdateAirportResponseConverter) context.getAttribute(UPDATE_AIRPORT_RESPONSE_CONVERTER);
        airportConverter = (AirportConverter) context.getAttribute(AIRPORT_CONVERTER);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
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
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var request = httpHelper.getRequestBody(req, CreateAirportRequest.class);

        airportValidationService.validateCreateRequest(request);

        var airport = createAirportRequestConverter.convert(request);

        var savedAirport = airportService.save(airport);

        var dto = createAirportResponseConverter.convert(savedAirport);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, dto);
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
        var id = parameterExtractor.extractId(req);

        if (id == null) {
            findAll(resp);
        } else {
            findById(resp, id);
        }
    }

    @PUT
    @Operation(tags = {"Airports"}, summary = "Обновление данных аэропорта по id",
            description = "Обновление аэропорта по id",
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
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var request = httpHelper.getRequestBody(req, UpdateAirportRequest.class);

        airportValidationService.validateUpdateRequest(request);

        var airport = updateAirportRequestConverter.convert(request);

        var updatedAirport = airportService.update(airport);

        var dto = updateAirportResponseConverter.convert(updatedAirport);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dto);
    }

    @DELETE
    @Operation(tags = {"Airports"}, summary = "Удаление аэропорта по id",
            description = "Удаление аэропорта по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id аэропорта", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Аэропорт не найден")})
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req,
                         @Parameter(hidden = true) HttpServletResponse resp) {
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req);

        requestParameterValidationService.validateId(id);

        airportService.delete(id);

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var airports = airportService.findAll();
        var airportDtos = airportConverter.convertAll(airports);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, airportDtos);
    }

    private void findById(HttpServletResponse resp, Long id) throws IOException {
        requestParameterValidationService.validateId(id);

        var airport = airportService.findById(id);

        var dto = airportConverter.convert(airport);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dto);
    }
}
