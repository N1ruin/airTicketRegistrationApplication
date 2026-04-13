package servlet;

import converter.ticket.TicketDtoConverter;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.TicketService;

import java.io.IOException;

import static constant.AttributeName.*;
import static constant.AttributeName.TICKET_SERVICE;

@WebServlet("/api/v1/ticket/actual")
public class ActualTicketServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(ActualTicketServlet.class);
    private HttpHelper httpHelper;
    private PermissionChecker permissionChecker;
    private TicketService ticketService;
    private TicketDtoConverter ticketDtoConverter;
    private SessionAttributeExtractor sessionAttributeExtractor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);
        ticketDtoConverter = (TicketDtoConverter) context.getAttribute(TICKET_DTO_CONVERTER);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isAdmin = permissionChecker.isAdmin(req);

        if (isAdmin) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var tickets = ticketService.findAllActualByUserId(currentUserId);

        var ticketDtos = ticketDtoConverter.convertAll(tickets);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, ticketDtos);
    }
}
