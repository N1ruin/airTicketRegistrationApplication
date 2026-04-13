package servlet;

import dto.passenger.CreatePassengerRequest;
import dto.passenger.UpdatePassengerRequest;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import converter.passenger.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.HttpHelper;
import service.PassengerService;
import validation.PassengerValidationService;
import validation.RequestParameterValidationService;

import java.io.IOException;

import static constant.AttributeName.*;

@WebServlet("/api/v1/passenger")
public class PassengerServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger(PassengerServlet.class);
    private HttpHelper httpHelper;
    private PassengerService passengerService;
    private RequestParameterExtractor parameterExtractor;
    private PassengerValidationService passengerValidationService;
    private CreatePassengerRequestConverter createPassengerRequestConverter;
    private CreatePassengerResponseConverter createPassengerResponseConverter;
    private PassengerDtoConverter passengerDtoConverter;
    private UpdatePassengerRequestConverter updatePassengerRequestConverter;
    private UpdatePassengerResponseConverter updatePassengerResponseConverter;
    private RequestParameterValidationService requestParameterValidationService;
    private PermissionChecker permissionChecker;
    private SessionAttributeExtractor sessionAttributeExtractor;

    @Override
    public void init(ServletConfig config) {
        log.info("Servlet {} initialization start", getClass().getSimpleName());

        var context = config.getServletContext();

        httpHelper = (HttpHelper) context.getAttribute(HTTP_HELPER);
        passengerService = (PassengerService) context.getAttribute(PASSENGER_SERVICE);
        parameterExtractor = (RequestParameterExtractor) context.getAttribute(REQUEST_PARAMETER_EXTRACTOR);
        passengerValidationService = (PassengerValidationService) context.getAttribute(PASSENGER_VALIDATION_SERVICE);
        createPassengerRequestConverter =
                (CreatePassengerRequestConverter) context.getAttribute(CREATE_PASSENGER_REQUEST_CONVERTER);
        createPassengerResponseConverter =
                (CreatePassengerResponseConverter) context.getAttribute(CREATE_PASSENGER_RESPONSE_CONVERTER);
        passengerDtoConverter = (PassengerDtoConverter) context.getAttribute(PASSENGER_DTO_CONVERTER);
        updatePassengerRequestConverter =
                (UpdatePassengerRequestConverter) context.getAttribute(UPDATE_PASSENGER_REQUEST_CONVERTER);
        updatePassengerResponseConverter =
                (UpdatePassengerResponseConverter) context.getAttribute(UPDATE_PASSENGER_RESPONSE_CONVERTER);
        requestParameterValidationService =
                (RequestParameterValidationService) context.getAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE);
        permissionChecker = (PermissionChecker) context.getAttribute(PERMISSION_CHECKER);
        sessionAttributeExtractor = (SessionAttributeExtractor) context.getAttribute(SESSION_ATTRIBUTE_EXTRACTOR);

        log.info("Servlet {} initialization finish", getClass().getSimpleName());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        var request = httpHelper.getRequestBody(req, CreatePassengerRequest.class);

        passengerValidationService.validateCreateRequest(request);

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var passenger = createPassengerRequestConverter.convert(request);
        passenger.setUserId(currentUserId);

        var savedPassenger = passengerService.save(passenger);

        var createPassengerResponse = createPassengerResponseConverter.convert(savedPassenger);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        httpHelper.writeResponseBody(resp, createPassengerResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var userId = parameterExtractor.extractUserId(req);
        var currentUserId = sessionAttributeExtractor.extractId(req);
        boolean isAdmin = permissionChecker.isAdmin(req);

        if (userId != null) {
            if (isAdmin || userId.equals(currentUserId)) {
                findAllByUserId(resp, userId);
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            if (isAdmin) {
                findAll(resp);
            } else {
                findAllByUserId(resp, currentUserId);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var request = httpHelper.getRequestBody(req, UpdatePassengerRequest.class);

        passengerValidationService.validateUpdateRequest(request);

        var passenger = updatePassengerRequestConverter.convert(request);

        var currentUserId = sessionAttributeExtractor.extractId(req);
        var updatedPassenger = passengerService.update(passenger, currentUserId);

        var dto = updatePassengerResponseConverter.convert(updatedPassenger);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dto);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        if (permissionChecker.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        var passengerId = parameterExtractor.extractId(req);

        requestParameterValidationService.validateId(passengerId);

        var userId = sessionAttributeExtractor.extractId(req);
        passengerService.delete(passengerId, userId);

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void findAllByUserId(HttpServletResponse resp, Long userId) throws IOException {
        requestParameterValidationService.validateId(userId);

        var passengers = passengerService.findAllByUserId(userId);

        var dtoResponse = passengerDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, dtoResponse);
    }

    private void findAll(HttpServletResponse resp) throws IOException {
        var passengers = passengerService.findAll();

        var passengerDtos = passengerDtoConverter.convertAll(passengers);

        resp.setStatus(HttpServletResponse.SC_OK);
        httpHelper.writeResponseBody(resp, passengerDtos);
    }
}
