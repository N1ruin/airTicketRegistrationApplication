package servlet;

import converter.airport.AirportConverter;
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
import service.AirportService;
import util.JsonHelper;

import java.io.IOException;

import static constant.ServletContextAttributeKey.*;
import static constant.ServletContextAttributeKey.JSON_HELPER;

@Path("/ticket-app/api/v1/airports")
public class AirportListServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(AirportListServlet.class);

    private AirportService airportService;
    private JsonHelper jsonHelper;
    private AirportConverter airportConverter;

    @Override
    public void init(ServletConfig config) {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();
        airportService = (AirportService) context.getAttribute(AIRPORT_SERVICE);
        airportConverter = (AirportConverter) context.getAttribute(AIRPORT_CONVERTER);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @GET
    @Operation(tags = {"Airports"}, summary = "Получение списка аэропортов",
            description = "Возвращает все аэропорты",
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

        var airports = airportService.findAll();
        var airportDtos = airportConverter.convertAll(airports);

        resp.setContentType("application/json");
        jsonHelper.writeBytes(resp.getOutputStream(), airportDtos);
    }
}
