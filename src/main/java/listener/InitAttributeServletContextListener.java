package listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import converter.user.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import converter.address.AddressConverter;
import converter.address.AddressDtoConverter;
import mapper.*;
import converter.airport.*;
import converter.flight.*;
import converter.passenger.*;
import converter.passsport.PassportDtoConverter;
import converter.passsport.PassportConverter;
import converter.ticket.CreateTicketRequestConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.ConnectionHelper;
import repository.TransactionHelper;
import repository.impl.*;
import sequrity.PasswordEncoder;
import service.*;
import servlet.PermissionChecker;
import servlet.RequestParameterExtractor;
import servlet.SessionAttributeExtractor;
import validation.*;


import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static constant.AttributeName.*;

public class InitAttributeServletContextListener implements ServletContextListener {
    private static final String DATASOURCE_PROPERTIES_PATH = "datasource.properties";
    private static final Logger log = LogManager.getLogger(InitAttributeServletContextListener.class);
    private final Properties properties = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Start attribute initialization");

        var context = sce.getServletContext();

        loadProperties();

        var dataSource = initHikariDataSource();
        context.setAttribute(DATA_SOURCE, dataSource);
        var migrationService = new MigrationService(dataSource);
        context.setAttribute(MIGRATION_SERVICE, migrationService);
        var passwordEncoder = new PasswordEncoder();
        var objectMapper = new ObjectMapper();
        var httpHelper = new HttpHelper(objectMapper);
        context.setAttribute(HTTP_HELPER, httpHelper);
        var connectionHelper = new ConnectionHelper(dataSource);
        var requestParameterExtractor = new RequestParameterExtractor();
        context.setAttribute(REQUEST_PARAMETER_EXTRACTOR, requestParameterExtractor);
        var requestParameterValidationService = new RequestParameterValidationService();
        context.setAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE, requestParameterValidationService);
        var transactionHelper = new TransactionHelper(connectionHelper);
        var sessionAttributeExtractor = new SessionAttributeExtractor();
        context.setAttribute(SESSION_ATTRIBUTE_EXTRACTOR, sessionAttributeExtractor);
        var permissionChecker = new PermissionChecker(sessionAttributeExtractor);
        context.setAttribute(PERMISSION_CHECKER, permissionChecker);

        var userResultSetMapper = new UserResultSetMapper();
        var userRepository = new UserRepositoryImpl(connectionHelper, userResultSetMapper);
        var userSignUpRequestConverter = new UserSignUpRequestConverter(passwordEncoder);
        context.setAttribute(USER_SIGN_UP_REQUEST_CONVERTER, userSignUpRequestConverter);
        var userSignUpResponseConverter = new UserSignUpResponseConverter();
        context.setAttribute(USER_SIGN_UP_RESPONSE_CONVERTER, userSignUpResponseConverter);
        var userValidationService = new UserValidationService();
        context.setAttribute(USER_VALIDATION_SERVICE, userValidationService);
        var userDtoConverter = new UserDtoConverter();
        context.setAttribute(USER_DTO_CONVERTER, userDtoConverter);
        var userService = new UserService(userRepository, transactionHelper, passwordEncoder, userDtoConverter);
        context.setAttribute(USER_SERVICE, userService);

        var addressValidationService = new AddressValidationService();
        context.setAttribute(ADDRESS_VALIDATION_SERVICE, addressValidationService);
        var addressResultSetMapper = new AddressResultSetMapper();
        var addressConverter = new AddressConverter();
        context.setAttribute(ADDRESS_CONVERTER, addressConverter);
        var addressDtoConverter = new AddressDtoConverter();
        context.setAttribute(ADDRESS_DTO_CONVERTER, addressDtoConverter);
        var addressRepository = new AddressRepositoryImpl(connectionHelper);
        var addressService = new AddressService(transactionHelper, addressRepository);
        context.setAttribute(ADDRESS_SERVICE, addressService);

        var airportValidationService =
                new AirportValidationService(addressValidationService, requestParameterValidationService);
        context.setAttribute(AIRPORT_VALIDATION_SERVICE, airportValidationService);
        var airportResultSetMapper = new AirportResultSetMapper(addressResultSetMapper);
        var airportRepository = new AirportRepositoryImpl(connectionHelper, airportResultSetMapper);
        var airportConverter = new AirportConverter(addressConverter);
        context.setAttribute(AIRPORT_CONVERTER, airportConverter);
        var createAirportRequestConverter = new CreateAirportRequestConverter(addressDtoConverter);
        context.setAttribute(CREATE_AIRPORT_REQUEST_CONVERTER, createAirportRequestConverter);
        var createAirportResponseConverter = new CreateAirportResponseConverter(addressConverter);
        context.setAttribute(CREATE_AIRPORT_RESPONSE_CONVERTER, createAirportResponseConverter);
        var airportService = new AirportService(addressService, airportRepository, transactionHelper);
        context.setAttribute(AIRPORT_SERVICE, airportService);
        var updateAirportRequestConverter = new UpdateAirportRequestConverter(addressDtoConverter);
        context.setAttribute(UPDATE_AIRPORT_REQUEST_CONVERTER, updateAirportRequestConverter);
        var updateAirportResponseConverter = new UpdateAirportResponseConverter(addressConverter);
        context.setAttribute(UPDATE_AIRPORT_RESPONSE_CONVERTER, updateAirportResponseConverter);
        var airportDtoConverter = new AirportDtoConverter(addressDtoConverter);
        context.setAttribute(AIRPORT_CONVERTER, airportConverter);

        var passportDtoConverter = new PassportDtoConverter();
        context.setAttribute(PASSPORT_DTO_CONVERTER, passportDtoConverter);
        var passportConverter = new PassportConverter();
        context.setAttribute(PASSPORT_CONVERTER, passportConverter);
        var passportValidationService = new PassportValidationService();
        context.setAttribute(PASSPORT_VALIDATION_SERVICE, passportValidationService);
        var passportResultSetMapper = new PassportResultSetMapper();
        var passportRepository = new PassportRepositoryImpl(connectionHelper, passportResultSetMapper);
        var passportService = new PassportService(transactionHelper, passportRepository);
        context.setAttribute(PASSPORT_SERVICE, passportService);

        var passengerResultSetMapper = new PassengerResultSetMapper(passportResultSetMapper);
        var passengerRepository = new PassengerRepositoryImpl(connectionHelper, passengerResultSetMapper);
        var passengerService = new PassengerService(passengerRepository, airportService, passportService, transactionHelper);
        context.setAttribute(PASSENGER_SERVICE, passengerService);
        var passengerValidationService =
                new PassengerValidationService(requestParameterValidationService, passportValidationService);
        context.setAttribute(PASSENGER_VALIDATION_SERVICE, passengerValidationService);
        var createPassengerRequestConverter = new CreatePassengerRequestConverter(passportConverter);
        context.setAttribute(CREATE_PASSENGER_REQUEST_CONVERTER, createPassengerRequestConverter);
        var passengerDtoConverter = new PassengerDtoConverter(passportDtoConverter);
        context.setAttribute(PASSENGER_DTO_CONVERTER, passengerDtoConverter);
        var createPassengerResponseConverter = new CreatePassengerResponseConverter();
        context.setAttribute(CREATE_PASSENGER_RESPONSE_CONVERTER, createPassengerResponseConverter);
        var updatePassengerRequestConverter = new UpdatePassengerRequestConverter(passportConverter);
        context.setAttribute(UPDATE_PASSENGER_REQUEST_CONVERTER, updatePassengerRequestConverter);
        var updatePassengerResponseConverter = new UpdatePassengerResponseConverter(passportDtoConverter);
        context.setAttribute(UPDATE_PASSENGER_RESPONSE_CONVERTER, updatePassengerResponseConverter);
        var passengerConverter = new PassengerConverter(passportConverter);

        var createFlightRequestConverter = new CreateFlightRequestConverter(airportDtoConverter);
        context.setAttribute(CREATE_FLIGHT_REQUEST_CONVERTER, createFlightRequestConverter);
        var createFlightResponseConverter = new CreateFlightResponseConverter(airportConverter);
        context.setAttribute(CREATE_FLIGHT_RESPONSE_CONVERTER, createFlightResponseConverter);
        var flightDtoConverter = new FlightConverter(airportConverter);
        context.setAttribute(FLIGHT_CONVERTER, flightDtoConverter);
        var flightValidationService = new FlightValidationService(requestParameterValidationService);
        context.setAttribute(FLIGHT_VALIDATION_SERVICE, flightValidationService);
        var updateFlightRequestConverter = new UpdateFlightRequestConverter();
        context.setAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER, updateFlightRequestConverter);
        var updateFlightResponseConverter = new UpdateFlightResponseConverter(airportConverter);
        context.setAttribute(UPDATE_FLIGHT_RESPONSE_CONVERTER, updateFlightResponseConverter);
        var flightResultSetMapper = new FlightResultSetMapper();
        var flightRepository = new FlightRepositoryImpl(flightResultSetMapper, connectionHelper);
        var flightService = new FlightService(flightRepository, transactionHelper, airportRepository);
        context.setAttribute(FLIGHT_SERVICE, flightService);
        var flightConverter = new FlightDtoConverter(airportDtoConverter);

        var ticketResultSetMapper = new TicketResultSetMapper(passportResultSetMapper);
        var ticketRepository = new TicketRepositoryImpl(connectionHelper, ticketResultSetMapper);
        var ticketService = new TicketService(transactionHelper, ticketRepository, passengerService, airportService,
                flightService);
        context.setAttribute(TICKET_SERVICE, ticketService);
        var ticketValidationService = new TicketValidationService();
        context.setAttribute(TICKET_VALIDATION_SERVICE, ticketValidationService);
        var createTicketRequestConverter = new CreateTicketRequestConverter(flightConverter, passengerConverter);
        context.setAttribute(CREATE_TICKET_REQUEST_CONVERTER, createTicketRequestConverter);

        log.info("Attribute initialization finish");
    }

    private void loadProperties() {
        log.info("Start properties loading");

        try (var inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(DATASOURCE_PROPERTIES_PATH)) {

            if (inputStream == null) {
                throw new IOException("Properties file not found with path %s".formatted(DATA_SOURCE));
            }

            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Property load error", e);
            throw new RuntimeException(e);
        }

        log.info("Properties loading finish");
    }

    private DataSource initHikariDataSource() {
        log.info("Start loading hikariCP configuration");
        var config = new HikariConfig(properties);
        log.info("HikariCP configuration loading finish");

        return new HikariDataSource(config);
    }
}
