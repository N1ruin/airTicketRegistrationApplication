package servlet;

import converter.ticket.TicketDtoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TicketService;
import util.CurrentUserHolder;
import util.JsonHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/ticket/actual")
@Path("/ticket-app/api/v1/ticket/actual")
public class ActualTicketServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(ActualTicketServlet.class);
    private TicketService ticketService;
    private TicketDtoConverter ticketDtoConverter;
    private JsonHelper jsonHelper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        ticketDtoConverter = (TicketDtoConverter) context.getAttribute(TICKET_DTO_CONVERTER);
        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @GET
    @Operation(tags = {"Tickets"}, summary = "Получение списка актуальных билетов пользователя",
            description = "Возвращает все актуальные билеты пользователя",
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        if (CurrentUserHolder.isAdmin()) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var tickets = ticketService.findAllActualByUserId();

        var ticketDtos = ticketDtoConverter.convertAll(tickets);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(ticketDtos).getBytes(StandardCharsets.UTF_8));
    }
}
