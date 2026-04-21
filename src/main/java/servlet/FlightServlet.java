package servlet;

import converter.flight.*;
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
import service.HttpHelper;
import validation.FlightValidationService;
import validation.RequestParameterValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/flight")
@Path("/ticket-app/api/v1/flight")
public class FlightServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(FlightServlet.class);
    private HttpHelper httpHelper;
    private FlightService flightService;
    private RequestParameterExtractor parameterExtractor;
    private FlightValidationService flightValidationService;
    private CreateFlightRequestConverter createFlightRequestConverter;
    private CreateFlightResponseConverter createFlightResponseConverter;
    private FlightConverter flightConverter;
    private UpdateFlightRequestConverter flightRequestConverter;
    private UpdateFlightResponseConverter updateFlightResponseConverter;
    private RequestParameterValidationService requestParameterValidationService;
    private PermissionChecker permissionChecker;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        flightService = (FlightService) context.getAttribute(FLIGHT_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        flightValidationService = (FlightValidationService) context.getAttribute(FLIGHT_VALIDATION_SERVICE);
        createFlightRequestConverter =
                (CreateFlightRequestConverter) context.getAttribute(CREATE_FLIGHT_REQUEST_CONVERTER);
        createFlightResponseConverter =
                (CreateFlightResponseConverter) context.getAttribute(CREATE_FLIGHT_RESPONSE_CONVERTER);
        flightConverter = (FlightConverter) context.getAttribute(FLIGHT_CONVERTER);
        flightRequestConverter =
                (UpdateFlightRequestConverter) context.getAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER);
        updateFlightResponseConverter =
                (UpdateFlightResponseConverter) context.getAttribute(UPDATE_FLIGHT_RESPONSE_CONVERTER);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
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
        var id = parameterExtractor.extractId(req);

        if (id == null) {
            findAll(resp);
        } else {
            findById(resp, id);
        }
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
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var request = httpHelper.getRequestBody(req, CreateFlightRequest.class);

        flightValidationService.validateCreateRequest(request);

        var flight = createFlightRequestConverter.convert(request);

        var savedFlight = flightService.save(flight);

        var dto = createFlightResponseConverter.convert(savedFlight);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, dto);
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
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var updateFlightRequest = httpHelper.getRequestBody(req, UpdateFlightRequest.class);

        flightValidationService.validateUpdateRequest(updateFlightRequest);

        var flight = flightRequestConverter.convert(updateFlightRequest);

        var updatedFlight = flightService.update(flight);

        var flightDto = updateFlightResponseConverter.convert(updatedFlight);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, flightDto);
    }

    @DELETE
    @Operation(tags = {"Flights"}, summary = "Удаление рейса по id",
            description = "Удаление рейса по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id рейса", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Рейс не найден")})
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req,
                         @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req);

        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpHelper.writeResponseBody(resp, "Parameter 'id' is required for deletion");
            return;
        }

        requestParameterValidationService.validateId(id);

        flightService.delete(id);

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var flights = flightService.findAll();

        var flightDtos = flightConverter.convertAll(flights);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, flightDtos);
    }

    private void findById(HttpServletResponse resp, Long id) throws IOException {
        requestParameterValidationService.validateId(id);

        var flight = flightService.findById(id);

        var flightDto = flightConverter.convert(flight);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, flightDto);
    }
}
