package servlet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.UserService;
import validation.RequestParameterValidationService;

import static constant.AttributeName.*;

@WebServlet("/admin/unblock")
@Path("/ticket-app/admin/unblock")
public class UnblockingServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UnblockingServlet.class);
    private UserService userService;
    private RequestParameterExtractor requestParameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        requestParameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Operation(tags = {"Users"}, summary = "Разблокировка пользователя по id", description = "Разблокирует пользователя",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "ID пользователя", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @PUT
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req,
                      @Parameter(hidden = true) HttpServletResponse resp) {
        var id = requestParameterExtractor.extractId(req);

        requestParameterValidationService.validateId(id);

        userService.unblock(id);

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
