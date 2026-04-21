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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TicketService;
import validation.RequestParameterValidationService;

import static constant.AttributeName.*;

@WebServlet("/api/v1/ticket/refund")
@Path("/ticket-app/api/v1/ticket/refund")
public class TicketRefundServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(TicketRefundServlet.class);
    private TicketService ticketService;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;
    private SessionAttributeExtractor sessionAttributeExtractor;
    private PermissionChecker permissionChecker;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @PUT
    @Operation(tags = {"Tickets"}, summary = "Возврат билета по id",
            description = "Вовзрат билета по id",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "Id билета", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Билет не найден")})
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) {
        var id = parameterExtractor.extractId(req);

        requestParameterValidationService.validateId(id);

        if (permissionChecker.isAdmin(req)) {
            ticketService.refund(id);

            return;
        } else {
            var currentUserId = sessionAttributeExtractor.extractId(req);
            ticketService.refund(id, currentUserId);
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
