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
import converter.ticket.CreateTicketResponseConverter;
import converter.ticket.TicketDtoConverter;
import converter.user.UserDtoConverter;
import converter.user.UserSignUpRequestConverter;
import converter.user.UserSignUpResponseConverter;
import converter.user.UserUpdateResponseConverter;
import domain.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mapper.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceConfiguration;
import repository.SessionHelper;
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
        var sessionFactory = initSessionFactory(dataSource);
        context.setAttribute(SESSION_FACTORY, sessionFactory);
        var passwordEncoder = new PasswordEncoder();
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        var httpHelper = new HttpHelper(objectMapper);
        context.setAttribute(HTTP_HELPER, httpHelper);
        var sessionHelper = new SessionHelper(sessionFactory);
        var requestParameterExtractor = new RequestParameterExtractor();
        context.setAttribute(REQUEST_PARAMETER_EXTRACTOR, requestParameterExtractor);
        var requestParameterValidationService = new RequestParameterValidationService();
        context.setAttribute(REQUEST_PARAMETER_VALIDATION_SERVICE, requestParameterValidationService);
        var transactionHelper = new TransactionHelper(sessionHelper);
        var sessionAttributeExtractor = new SessionAttributeExtractor();
        context.setAttribute(SESSION_ATTRIBUTE_EXTRACTOR, sessionAttributeExtractor);
        var permissionChecker = new PermissionChecker(sessionAttributeExtractor);
        context.setAttribute(PERMISSION_CHECKER, permissionChecker);

        var userRepository = new UserRepositoryImpl(sessionHelper);
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
        var userUpdateResponseConverter = new UserUpdateResponseConverter();
        context.setAttribute(USER_UPDATE_RESPONSE_CONVERTER, userUpdateResponseConverter);

        var addressValidationService = new AddressValidationService();
        context.setAttribute(ADDRESS_VALIDATION_SERVICE, addressValidationService);
        var addressConverter = new AddressConverter();
        context.setAttribute(ADDRESS_CONVERTER, addressConverter);
        var addressDtoConverter = new AddressDtoConverter();
        context.setAttribute(ADDRESS_DTO_CONVERTER, addressDtoConverter);
        var addressRepository = new AddressRepositoryImpl(sessionHelper);
        var addressService = new AddressService(transactionHelper, addressRepository);
        context.setAttribute(ADDRESS_SERVICE, addressService);

        var airportValidationService =
                new AirportValidationService(addressValidationService, requestParameterValidationService);
        context.setAttribute(AIRPORT_VALIDATION_SERVICE, airportValidationService);
        var airportRepository = new AirportRepositoryImpl(sessionHelper);
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
        var passportRepository = new PassportRepositoryImpl(sessionHelper);
        var passportService = new PassportService(transactionHelper, passportRepository);
        context.setAttribute(PASSPORT_SERVICE, passportService);

        var passengerRepository = new PassengerRepositoryImpl(sessionHelper);
        var passengerService = new PassengerService(passengerRepository, airportService, passportService, transactionHelper);
        context.setAttribute(PASSENGER_SERVICE, passengerService);
        var passengerValidationService =
                new PassengerValidationService(requestParameterValidationService, passportValidationService);
        context.setAttribute(PASSENGER_VALIDATION_SERVICE, passengerValidationService);
        var createPassengerRequestConverter = new CreatePassengerRequestConverter(passportConverter);
        context.setAttribute(CREATE_PASSENGER_REQUEST_CONVERTER, createPassengerRequestConverter);
        var passengerDtoConverter = new PassengerDtoConverter(passportDtoConverter);
        context.setAttribute(PASSENGER_DTO_CONVERTER, passengerDtoConverter);
        var createPassengerResponseConverter = new CreatePassengerResponseConverter(passportDtoConverter);
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
        var updateFlightRequestConverter = new UpdateFlightRequestConverter(airportDtoConverter);
        context.setAttribute(UPDATE_FLIGHT_REQUEST_CONVERTER, updateFlightRequestConverter);
        var updateFlightResponseConverter = new UpdateFlightResponseConverter(airportConverter);
        context.setAttribute(UPDATE_FLIGHT_RESPONSE_CONVERTER, updateFlightResponseConverter);
        var flightRepository = new FlightRepositoryImpl(sessionHelper);
        var flightService = new FlightService(flightRepository, transactionHelper, airportService);
        context.setAttribute(FLIGHT_SERVICE, flightService);
        var flightConverter = new FlightDtoConverter(airportDtoConverter);

        var ticketRepository = new TicketRepositoryImpl(sessionHelper);
        var ticketService = new TicketService(transactionHelper, ticketRepository, passengerService, airportService,
                flightService);
        context.setAttribute(TICKET_SERVICE, ticketService);
        var ticketValidationService = new TicketValidationService();
        context.setAttribute(TICKET_VALIDATION_SERVICE, ticketValidationService);
        var createTicketRequestConverter = new CreateTicketRequestConverter(flightConverter, passengerConverter);
        context.setAttribute(CREATE_TICKET_REQUEST_CONVERTER, createTicketRequestConverter);
        var createTicketResponseConverter = new CreateTicketResponseConverter(flightDtoConverter, passengerDtoConverter);
        context.setAttribute(CREATE_TICKET_RESPONSE_CONVERTER, createTicketResponseConverter);
        var ticketDtoConverter = new TicketDtoConverter(flightDtoConverter, passengerDtoConverter);
        context.setAttribute(TICKET_DTO_CONVERTER, ticketDtoConverter);

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

    private SessionFactory initSessionFactory(DataSource dataSource) {
        return new HibernatePersistenceConfiguration("tickets")
                .managedClass(Address.class)
                .managedClass(Airport.class)
                .managedClass(Flight.class)
                .managedClass(Passenger.class)
                .managedClass(Passport.class)
                .managedClass(Ticket.class)
                .managedClass(User.class)
                .property("jakarta.persistence.nonJtaDataSource", dataSource)
                .property(AvailableSettings.DEFAULT_SCHEMA, "tickets_application")
                .showSql(true, true, true)
                .createEntityManagerFactory();
    }
}
