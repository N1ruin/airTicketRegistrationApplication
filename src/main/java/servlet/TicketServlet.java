package servlet;

import dto.ticket.CreateTicketRequest;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import converter.ticket.*;
import converter.ticket.CreateTicketRequestConverter;
import converter.ticket.CreateTicketResponseConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.TicketService;
import validation.RequestParameterValidationService;
import validation.TicketValidationService;

import java.io.IOException;

import static constant.AttributeName.*;
import static constant.AttributeName.REQUEST_PARAMETER_VALIDATION_SERVICE;

@WebServlet("/api/v1/ticket/")
public class TicketServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(TicketServlet.class);
    private HttpHelper httpHelper;
    private TicketService ticketService;
    private TicketValidationService ticketValidationService;
    private CreateTicketRequestConverter createTicketRequestConverter;
    private CreateTicketResponseConverter createTicketResponseConverter;
    private TicketDtoConverter ticketDtoConverter;
    private RequestParameterValidationService parameterValidationService;
    private PermissionChecker permissionChecker;
    private SessionAttributeExtractor sessionAttributeExtractor;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        ticketValidationService = (TicketValidationService) context.getAttribute(TICKET_VALIDATION_SERVICE);
        createTicketRequestConverter =
                (CreateTicketRequestConverter) context.getAttribute(CREATE_TICKET_REQUEST_CONVERTER);
        createTicketResponseConverter =
                (CreateTicketResponseConverter) context.getAttribute(CREATE_TICKET_RESPONSE_CONVERTER);
        ticketDtoConverter = (TicketDtoConverter) context.getAttribute(TICKET_DTO_CONVERTER);
        parameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var request = httpHelper.getRequestBody(req, CreateTicketRequest.class);

        ticketValidationService.validateCreateRequest(request);

        var ticket = createTicketRequestConverter.convert(request);

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var savedTicket = ticketService.save(ticket, currentUserId);

        var dto = createTicketResponseConverter.convert(savedTicket);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, dto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var currentUserId = sessionAttributeExtractor.extractId(req);
        boolean isAdmin = permissionChecker.isAdmin(req);

        if (isAdmin) {
            findAll(resp);
        } else {
            findAllByUserId(resp, currentUserId);
        }
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var tickets = ticketService.findAll();

        var ticketDtos = ticketDtoConverter.convertAll(tickets);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, ticketDtos);
    }

    private void findAllByUserId(HttpServletResponse resp, Long id) throws IOException {
        parameterValidationService.validateId(id);

        var tickets = ticketService.findAllByUserId(id);

        var ticketDto = ticketDtoConverter.convertAll(tickets);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, ticketDto);
    }
}
