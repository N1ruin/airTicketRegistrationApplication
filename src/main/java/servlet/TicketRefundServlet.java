package servlet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TicketService;
import util.CurrentUserHolder;
import util.RequestParameterExtractor;
import validation.RequestParameterValidationService;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/ticket/refund")
@Path("/ticket-app/api/v1/ticket/refund")
public class TicketRefundServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(TicketRefundServlet.class);
    private TicketService ticketService;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @PATCH
    @Operation(tags = {"Tickets"}, summary = "Возврат билета по id",
            description = "Возврат билета по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id билета", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Билет не найден")})
    @Override
    public void doPatch(@Parameter(hidden = true) HttpServletRequest req,
                        @Parameter(hidden = true) HttpServletResponse resp) {
        var id = parameterExtractor.extractId(req, true);

        requestParameterValidationService.validateId(id);

        if (CurrentUserHolder.isAdmin()) {
            ticketService.refund(id);
        } else {
            ticketService.refundByCurrentUser(id);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
