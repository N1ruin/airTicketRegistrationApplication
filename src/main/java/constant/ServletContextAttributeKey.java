package constant;

public class ServletContextAttributeKey {

    private ServletContextAttributeKey() {
    }

    public static final String DATA_SOURCE = "dataSource";
    public static final String REQUEST_PARAMETER_EXTRACTOR = "requestParameterExtractor";
    public static final String REQUEST_PARAMETER_VALIDATION_SERVICE = "requestParameterValidationService";
    public static final String PASSWORD_ENCODER = "passwordEncoder";
    public static final String OBJECT_MAPPER = "objectMapper";
    public static final String PROPERTIES = "properties";
    public static final String CONNECTION_HELPER = "connectionHelper";
    public static final String TRANSACTION_HELPER = "transactionHelper";
    public static final String VALIDATOR_FACTORY = "validatorFactory";
    public static final String VALIDATION_SERVICE = "validationService";
    public static final String JSON_HELPER = "jsonHelper";

    public static final String USER_SERVICE = "userService";
    public static final String USER_SIGN_UP_REQUEST_CONVERTER = "userSignUpRequestConverter";
    public static final String USER_DTO_CONVERTER = "userDtoConverter";
    public static final String USER_RESULT_SET_MAPPER = "userResultSetMapper";
    public static final String USER_REPOSITORY = "userRepository";

    public static final String AIRPORT_SERVICE = "airportService";
    public static final String CREATE_AIRPORT_REQUEST_CONVERTER = "createAirportRequestConverter";
    public static final String UPDATE_AIRPORT_REQUEST_CONVERTER = "updateAirportRequestConverter";
    public static final String AIRPORT_CONVERTER = "airportConverter`";
    public static final String AIRPORT_RESULT_SET_MAPPER = "airportResultSetMapper";
    public static final String AIRPORT_REPOSITORY = "airportRepository";
    public static final String FAVORITE_AIRPORTS_REPOSITORY = "favoriteAirportsRepository";

    public static final String ADDRESS_RESULT_SET_MAPPER = "addressResultSetMapper";
    public static final String ADDRESS_CONVERTER = "addressConverter";
    public static final String ADDRESS_DTO_CONVERTER = "addressDtoConverter";
    public static final String ADDRESS_SERVICE = "addressService";
    public static final String ADDRESS_REPOSITORY = "addressRepository";

    public static final String PASSENGER_SERVICE = "passengerService";
    public static final String PASSENGER_VALIDATION_SERVICE = "passengerValidationService";
    public static final String CREATE_PASSENGER_REQUEST_CONVERTER = "createPassengerRequestConverter";
    public static final String PASSENGER_DTO_CONVERTER = "passengerDtoConverter";
    public static final String UPDATE_PASSENGER_REQUEST_CONVERTER = "updatePassengerRequestConverter";
    public static final String PASSENGER_RESULT_SET_MAPPER = "passengerResultSetMapper";
    public static final String PASSENGER_REPOSITORY = "passengerRepository";

    public static final String PASSPORT_DTO_CONVERTER = "passportDtoConverter";
    public static final String PASSPORT_CONVERTER = "passportConverter";
    public static final String PASSPORT_SERVICE = "passportService";
    public static final String PASSPORT_RESULT_SET_MAPPER = "passportResultSetMapper";
    public static final String PASSPORT_REPOSITORY = "passportRepository";

    public static final String CREATE_FLIGHT_REQUEST_CONVERTER = "createFlightRequestConverter";
    public static final String FLIGHT_CONVERTER = "flightDtoConverter";
    public static final String UPDATE_FLIGHT_REQUEST_CONVERTER = "updateFlightRequestConverter";
    public static final String FLIGHT_SERVICE = "flightService";
    public static final String FLIGHT_RESULT_SET_MAPPER = "flightResultSetMapper";
    public static final String FLIGHT_REPOSITORY = "flightRepository";

    public static final String TICKET_SERVICE = "ticketService";
    public static final String CREATE_TICKET_REQUEST_CONVERTER = "createTicketRequestConverter";
    public static final String TICKET_DTO_CONVERTER = "ticketDtoConverter";
    public static final String TICKET_RESULT_SET_MAPPER = "ticketResultSetMapper";
    public static final String TICKET_REPOSITORY = "tickerRepository";
}
