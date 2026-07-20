package servlet;

import converter.flight.CreateFlightRequestConverter;
import converter.flight.FlightConverter;
import converter.flight.UpdateFlightRequestConverter;
import dto.flight.CreateFlightRequest;
import dto.flight.UpdateFlightRequest;
import exception.ApplicationException;
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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.FlightService;
import util.JsonHelper;
import util.RequestParameterExtractor;
import validation.service.ValidationService;

import java.io.IOException;

import static constant.ServletContextAttributeKey.*;
import static constant.ServletContextAttributeKey.CREATE_FLIGHT_REQUEST_CONVERTER;
import static constant.ServletContextAttributeKey.FLIGHT_CONVERTER;
import static constant.ServletContextAttributeKey.UPDATE_FLIGHT_REQUEST_CONVERTER;
import static constant.ServletContextAttributeKey.VALIDATION_SERVICE;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@Path("/ticket-app/admin/api/v1/flight")
public class AdminFlightServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(AdminFlightServlet.class);
    private FlightService flightService;
    private RequestParameterExtractor parameterExtractor;
    private CreateFlightRequestConverter createFlightRequestConverter;
    private FlightConverter flightConverter;
    private UpdateFlightRequestConverter flightRequestConverter;
    private JsonHelper jsonHelper;
    private ValidationService validationService;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        flightService = (FlightService) context.getAttribute(FLIGHT_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        createFlightRequestConverter =
                (CreateFlightRequestConverter) context.getAttribute(CREATE_FLIGHT_REQUEST_CONVERTER);
        flightConverter = (FlightConverter) context.getAttribute(FLIGHT_CONVERTER);
        flightRequestConverter =
                (UpdateFlightRequestConverter) context.getAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
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
        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, CreateFlightRequest.class);

        validationService.validate(request);

        var flight = createFlightRequestConverter.convert(request);

        var savedFlight = flightService.create(flight);

        var dto = flightConverter.convert(savedFlight);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), dto);
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
        var body = new String(req.getInputStream().readAllBytes());
        var updateFlightRequest = jsonHelper.fromJson(body, UpdateFlightRequest.class);

        validationService.validate(updateFlightRequest);

        var flight = flightRequestConverter.convert(updateFlightRequest);

        var updatedFlight = flightService.update(flight);

        var flightDto = flightConverter.convert(updatedFlight);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), flightDto);
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
                         @Parameter(hidden = true) HttpServletResponse resp) {
        var id = parameterExtractor.extractParameter(req, "id", true, Long::parseLong);

        if (id == null) {
            throw new ApplicationException("Parameter 'id' is required for deletion", SC_BAD_REQUEST);
        }

        flightService.delete(id);

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
