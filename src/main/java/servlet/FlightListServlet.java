package servlet;

import converter.flight.FlightConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.FlightService;
import util.JsonHelper;

import java.io.IOException;

import static constant.ServletContextAttributeKey.*;
import static constant.ServletContextAttributeKey.FLIGHT_CONVERTER;

@Path("/ticket-app/api/v1/flights")
public class FlightListServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(FlightListServlet.class);
    private FlightService flightService;
    private FlightConverter flightConverter;
    private JsonHelper jsonHelper;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        flightService = (FlightService) context.getAttribute(FLIGHT_SERVICE);
        flightConverter = (FlightConverter) context.getAttribute(FLIGHT_CONVERTER);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @GET
    @Operation(tags = {"Flights"}, summary = "Получение данных всех рейсов",
            description = "Возвращает данные всех рейсы",
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
        var flights = flightService.findAll();

        var flightDtos = flightConverter.convertAll(flights);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), flightDtos);
    }
}
