package listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import converter.address.AddressConverter;
import converter.address.AddressDtoConverter;
import converter.airport.*;
import converter.flight.*;
import converter.passenger.*;
import converter.passsport.PassportConverter;
import converter.passsport.PassportDtoConverter;
import converter.ticket.CreateTicketRequestConverter;
import converter.ticket.TicketDtoConverter;
import converter.user.UserDtoConverter;
import converter.user.UserSignUpRequestConverter;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mapper.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.ConnectionHelper;
import util.TransactionHelper;
import repository.impl.*;
import security.PasswordEncoder;
import service.*;
import util.RequestParameterExtractor;
import util.JsonHelper;
import validation.*;
import validation.validator.ValidationService;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static constant.ServletContextAttributeKey.*;

public class InitAttributeServletContextListener implements ServletContextListener {
    private static final String DATASOURCE_PROPERTIES_PATH = "datasource.properties";
    private static final Logger log = LogManager.getLogger(InitAttributeServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Attribute initialization started");

        var context = sce.getServletContext();

        var properties = loadProperties();
        context.setAttribute(PROPERTIES, properties);

        var dataSource = initHikariDataSource(properties);
        context.setAttribute(DATA_SOURCE, dataSource);
        var validationService = new ValidationService();
        context.setAttribute(VALIDATION_SERVICE, validationService);
        var objectMapper = new ObjectMapper();
        context.setAttribute(OBJECT_MAPPER, objectMapper);
        objectMapper.registerModule(new JavaTimeModule());
        var jsonUtil = new JsonHelper(objectMapper);
        context.setAttribute(JSON_HELPER, jsonUtil);
        var connectionHelper = new ConnectionHelper(dataSource);
        context.setAttribute(CONNECTION_HELPER, connectionHelper);
        var requestParameterExtractor = new RequestParameterExtractor();
        context.setAttribute(REQUEST_PARAMETER_EXTRACTOR, requestParameterExtractor);
        var requestParameterValidationService = new RequestParameterValidationService();
        context.setAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE, requestParameterValidationService);
        var transactionHelper = new TransactionHelper(connectionHelper);
        context.setAttribute(TRANSACTION_HELPER, connectionHelper);

        var userResultSetMapper = new UserResultSetMapper();
        context.setAttribute(USER_RESULT_SET_MAPPER, connectionHelper);
        var userRepository = new UserRepositoryImpl(connectionHelper, userResultSetMapper);
        context.setAttribute(USER_REPOSITORY, connectionHelper);
        var passwordEncoder = new PasswordEncoder();
        context.setAttribute(PASSWORD_ENCODER, passwordEncoder);
        var userSignUpRequestConverter = new UserSignUpRequestConverter(passwordEncoder);
        context.setAttribute(USER_SIGN_UP_REQUEST_CONVERTER, userSignUpRequestConverter);
        var userDtoConverter = new UserDtoConverter();
        context.setAttribute(USER_DTO_CONVERTER, userDtoConverter);
        var userService = new UserService(userRepository, transactionHelper, passwordEncoder);
        context.setAttribute(USER_SERVICE, userService);

        var addressResultSetMapper = new AddressResultSetMapper();
        context.setAttribute(ADDRESS_RESULT_SET_MAPPER, addressResultSetMapper);
        var addressConverter = new AddressConverter();
        context.setAttribute(ADDRESS_CONVERTER, addressConverter);
        var addressDtoConverter = new AddressDtoConverter();
        context.setAttribute(ADDRESS_DTO_CONVERTER, addressDtoConverter);
        var addressRepository = new AddressRepositoryImpl(connectionHelper, addressResultSetMapper);
        context.setAttribute(ADDRESS_REPOSITORY, connectionHelper);
        var addressService = new AddressService(transactionHelper, addressRepository);
        context.setAttribute(ADDRESS_SERVICE, addressService);

        var airportResultSetMapper = new AirportResultSetMapper(addressResultSetMapper);
        context.setAttribute(AIRPORT_RESULT_SET_MAPPER, connectionHelper);
        var airportRepository = new AirportRepositoryImpl(connectionHelper, airportResultSetMapper);
        context.setAttribute(AIRPORT_REPOSITORY, connectionHelper);
        var airportConverter = new AirportConverter();
        context.setAttribute(AIRPORT_CONVERTER, airportConverter);
        var createAirportRequestConverter = new CreateAirportRequestConverter(addressDtoConverter);
        context.setAttribute(CREATE_AIRPORT_REQUEST_CONVERTER, createAirportRequestConverter);
        var airportService = new AirportService(addressService, airportRepository, transactionHelper);
        context.setAttribute(AIRPORT_SERVICE, airportService);
        var updateAirportRequestConverter = new UpdateAirportRequestConverter(addressDtoConverter);
        context.setAttribute(UPDATE_AIRPORT_REQUEST_CONVERTER, updateAirportRequestConverter);
        var favoriteAirportsRepository = new FavoriteAirportsRepositoryImpl(connectionHelper, airportResultSetMapper);
        context.setAttribute(FAVORITE_AIRPORTS_REPOSITORY, favoriteAirportsRepository);

        var passportResultSetMapper = new PassportResultSetMapper();
        context.setAttribute(PASSPORT_RESULT_SET_MAPPER, passportResultSetMapper);
        var passportDtoConverter = new PassportDtoConverter();
        context.setAttribute(PASSPORT_DTO_CONVERTER, passportDtoConverter);
        var passportConverter = new PassportConverter();
        context.setAttribute(PASSPORT_CONVERTER, passportConverter);
        context.setAttribute(PASSPORT_RESULT_SET_MAPPER, connectionHelper);
        var passportRepository = new PassportRepositoryImpl(connectionHelper, passportResultSetMapper);
        context.setAttribute(PASSPORT_REPOSITORY, connectionHelper);
        var passportService = new PassportService(transactionHelper, passportRepository);
        context.setAttribute(PASSPORT_SERVICE, passportService);

        var passengerResultSetMapper = new PassengerResultSetMapper(passportResultSetMapper);
        context.setAttribute(PASSENGER_RESULT_SET_MAPPER, connectionHelper);
        var passengerRepository = new PassengerRepositoryImpl(connectionHelper, passengerResultSetMapper);
        context.setAttribute(PASSENGER_REPOSITORY, connectionHelper);
        var passengerService = new PassengerService(passengerRepository, favoriteAirportsRepository,
                airportService, passportService, transactionHelper);
        context.setAttribute(PASSENGER_SERVICE, passengerService);
        var createPassengerRequestConverter = new PassportDtoToPassengerConverter(passportConverter);
        context.setAttribute(CREATE_PASSENGER_REQUEST_CONVERTER, createPassengerRequestConverter);
        var passengerDtoConverter = new PassengerDtoConverter();
        context.setAttribute(PASSENGER_DTO_CONVERTER, passengerDtoConverter);
        var updatePassengerRequestConverter = new UpdatePassengerRequestConverter(passportConverter);
        context.setAttribute(UPDATE_PASSENGER_REQUEST_CONVERTER, updatePassengerRequestConverter);

        var createFlightRequestConverter = new CreateFlightRequestConverter();
        context.setAttribute(CREATE_FLIGHT_REQUEST_CONVERTER, createFlightRequestConverter);
        var flightDtoConverter = new FlightConverter();
        context.setAttribute(FLIGHT_CONVERTER, flightDtoConverter);
        var updateFlightRequestConverter = new UpdateFlightRequestConverter();
        context.setAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER, updateFlightRequestConverter);
        var flightResultSetMapper = new FlightResultSetMapper();
        context.setAttribute(FLIGHT_RESULT_SET_MAPPER, connectionHelper);
        var flightRepository = new FlightRepositoryImpl(flightResultSetMapper, connectionHelper);
        context.setAttribute(FLIGHT_REPOSITORY, connectionHelper);
        var flightService = new FlightService(flightRepository, transactionHelper, airportService);
        context.setAttribute(FLIGHT_SERVICE, flightService);

        var ticketResultSetMapper = new TicketResultSetMapper();
        context.setAttribute(TICKET_RESULT_SET_MAPPER, connectionHelper);
        var ticketRepository = new TicketRepositoryImpl(connectionHelper, ticketResultSetMapper);
        context.setAttribute(TICKET_REPOSITORY, connectionHelper);
        var ticketService = new TicketService(transactionHelper, ticketRepository, passengerService, airportService,
                flightService);
        context.setAttribute(TICKET_SERVICE, ticketService);
        var createTicketRequestConverter = new CreateTicketRequestConverter();
        context.setAttribute(CREATE_TICKET_REQUEST_CONVERTER, createTicketRequestConverter);
        var ticketDtoConverter = new TicketDtoConverter();
        context.setAttribute(TICKET_DTO_CONVERTER, ticketDtoConverter);

        log.info("Attribute initialization finished");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application destroy started");

        var context = sce.getServletContext();

        log.info("Hikari data source closing started");

        ((HikariDataSource) context.getAttribute(DATA_SOURCE))
                .close();

        log.info("Hikari data source closing finished");

        log.info("Validator factory closing started");

        ((ValidationService) context.getAttribute(VALIDATION_SERVICE))
                .close();

        log.info("Validator factory closing finished");

        log.info("Application destroy finished");
    }

    private Properties loadProperties() {
        log.info("Start properties loading started");

        var properties = new Properties();

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

        log.info("Properties loading finished");

        return properties;
    }

    private DataSource initHikariDataSource(Properties properties) {
        log.info("hikariCP configuration loading started");

        var config = new HikariConfig(properties);

        log.info("HikariCP configuration loading finished");

        return new HikariDataSource(config);
    }
}
