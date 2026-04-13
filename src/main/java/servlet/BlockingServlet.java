package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.UserService;
import validation.RequestParameterValidationService;

import static constant.AttributeName.*;

@WebServlet("/admin/block")
public class BlockingServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(BlockingServlet.class);
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        var id = requestParameterExtractor.extractId(req);

        requestParameterValidationService.validateId(id);

        userService.block(id);

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
