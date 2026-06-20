package servlet;

import converter.ticket.CreateTicketRequestConverter;
import converter.ticket.TicketDtoConverter;
import dto.ticket.CreateTicketRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TicketService;
import util.CurrentUserHolder;
import util.JsonHelper;
import validation.validator.ValidationService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static constant.ServletContextAttributeKey.*;

@WebServlet("/api/v1/ticket")
@Path("/ticket-app/api/v1/ticket")
public class TicketServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(TicketServlet.class);
    private JsonHelper jsonHelper;
    private TicketService ticketService;
    private ValidationService validationService;
    private CreateTicketRequestConverter createTicketRequestConverter;
    private TicketDtoConverter ticketDtoConverter;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        jsonHelper = (JsonHelper) context.getAttribute(JSON_HELPER);
        ticketService = (TicketService) context.getAttribute(TICKET_SERVICE);
        validationService = (ValidationService) context.getAttribute(VALIDATION_SERVICE);
        createTicketRequestConverter =
                (CreateTicketRequestConverter) context.getAttribute(CREATE_TICKET_REQUEST_CONVERTER);
        ticketDtoConverter = (TicketDtoConverter) context.getAttribute(TICKET_DTO_CONVERTER);

        log.info("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @POST
    @Operation(tags = {"Tickets"}, summary = "Создание билета",
            description = "Создание билета",
            requestBody = @RequestBody(description = "Данные билета", required = true,
                    content = @Content(schema = @Schema(implementation = CreateTicketRequest.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Полет или пассажир не найден"),
                    @ApiResponse(responseCode = "409", description = "Пассажир с таким билетом уже существует")})
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req,
                       @Parameter(hidden = true) HttpServletResponse resp) throws IOException {

        var body = new String(req.getInputStream().readAllBytes());
        var request = jsonHelper.fromJson(body, CreateTicketRequest.class);

        validationService.validate(request);

        var ticket = createTicketRequestConverter.convert(request);

        var savedTicket = ticketService.create(ticket);

        var dto = ticketDtoConverter.convert(savedTicket);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonHelper.toJson(dto).getBytes(StandardCharsets.UTF_8));
    }

    @GET
    @Operation(tags = {"Tickets"}, summary = "Получение списка билетов",
            description = "Возвращает все билеты пользователя, для администратора возвращает все билеты системы",
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Билет не найден")})
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) throws IOException {
        String responseBodyJson;
        if (CurrentUserHolder.isAdmin()) {
            responseBodyJson = findAll();
        } else {
            responseBodyJson = findAllByCurrentUserId();
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseBodyJson.getBytes(StandardCharsets.UTF_8));
    }

    private String findAll() {
        var tickets = ticketService.findAll();

        var ticketDtos = ticketDtoConverter.convertAll(tickets);

        return jsonHelper.toJson(ticketDtos);
    }

    private String findAllByCurrentUserId() {
        var tickets = ticketService.findAllByCurrentUserId();

        var ticketDto = ticketDtoConverter.convertAll(tickets);

        return jsonHelper.toJson(ticketDto);
    }
}
