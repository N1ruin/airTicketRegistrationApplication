package servlet;

import converter.user.UserDtoConverter;
import dto.user.UpdateUserRequest;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.UserService;
import validation.RequestParameterValidationService;
import validation.UserValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/user")
public class UserServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(UserServlet.class);
    private HttpHelper httpHelper;
    private UserService userService;
    private RequestParameterExtractor parameterExtractor;
    private RequestParameterValidationService requestParameterValidationService;
    private UserDtoConverter userDtoConverter;
    private PermissionChecker permissionChecker;
    private UserValidationService userValidationService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        userService = (UserService) context.getAttribute(USER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        userDtoConverter = (UserDtoConverter) context.getAttribute(USER_DTO_CONVERTER);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        userValidationService = (UserValidationService) context.getAttribute(USER_VALIDATION_SERVICE);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var id = parameterExtractor.extractId(req);

        if (id == null) {
            findAll(resp);
        } else {
            findById(resp, id);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var request = httpHelper.getRequestBody(req, UpdateUserRequest.class);

        userValidationService.validateUpdateRequest(request);

        userService.update(request.id(), request.newPassword(), request.firstName(), request.lastName(),
                request.fatherName());

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void findById(HttpServletResponse resp, long id) throws IOException {
        requestParameterValidationService.validateId(id);

        var user = userService.findById(id);

        var userDto = userDtoConverter.convert(user);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDto);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var passengers = userService.findAll();

        var userDtos = userDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, userDtos);
    }
}
