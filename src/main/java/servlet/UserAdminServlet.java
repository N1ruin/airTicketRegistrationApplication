package servlet;

import exception.ApplicationException;
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
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.UserService;
import util.RequestParameterExtractor;

import static constant.ServletContextAttributeKey.*;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@WebServlet("/admin/api/v1/user/*")
@Path("/ticket-app/admin/user/*")
public class UserAdminServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UserAdminServlet.class);

    private UserService userService;
    private RequestParameterExtractor requestParameterExtractor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.debug("Servlet {} initialization started", getClass().getSimpleName());

        var context = config.getServletContext();

        userService = (UserService) context.getAttribute(USER_SERVICE);
        requestParameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);

        log.debug("Servlet {} initialization finished", getClass().getSimpleName());
    }

    @Operation(tags = {"Users"}, summary = "Блокировка/разблокировка пользователя по id",
            description = "Блокирует пользователя",
            parameters = {@Parameter(name = "id", in = ParameterIn.QUERY, description = "ID пользователя", example = "1",
                    schema = @Schema(type = "integer", format = "int64"))},
            responses = {@ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")})
    @PATCH
    @Override
    public void doPatch(@Parameter(hidden = true) HttpServletRequest req,
                        @Parameter(hidden = true) HttpServletResponse resp) {
        var id = requestParameterExtractor.extractParameter(req, "id", true, Long::parseLong);
        var action = req.getPathInfo();

        if (action == null || action.equals("/")) {
            throw new ApplicationException("Action is required. Use /block or /unblock", SC_BAD_REQUEST);
        }

        switch (action) {
            case "/block" -> userService.block(id);
            case "/unblock" -> userService.unblock(id);
            default -> throw new ApplicationException("Unknown action: " + action + ". Use /block or /unblock",
                    SC_BAD_REQUEST);
        }
    }
}
