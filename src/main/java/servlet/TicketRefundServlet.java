package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TicketService;
import validation.RequestParameterValidationService;

import static constant.AttributeName.*;

@WebServlet("/api/v1/ticket/refund/")
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
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
