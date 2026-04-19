package mapper;

import domain.*;
import exception.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketResultSetMapper implements ResultSetMapper<Optional<Ticket>>, ResultSetListMapper<Ticket> {
    private final PassportResultSetMapper passportResultSetMapper;

    public TicketResultSetMapper(PassportResultSetMapper passportResultSetMapper) {
        this.passportResultSetMapper = passportResultSetMapper;
    }

    @Override
    public Optional<Ticket> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    @Override
    public List<Ticket> mapList(ResultSet resultSet) throws SQLException {
        var tickets = new ArrayList<Ticket>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(tickets::add);
        }
        return tickets;
    }

    private Optional<Ticket> mapRow(ResultSet resultSet) throws SQLException {
        var departureAddress = new Address();
        departureAddress.setId(resultSet.getLong("departure_address_id"));
        departureAddress.setCountry(resultSet.getString("departure_address_country"));
        departureAddress.setCity(resultSet.getString("departure_address_city"));
        departureAddress.setStreet(resultSet.getString("departure_address_street"));
        departureAddress.setHouseNumber(resultSet.getInt("departure_address_house_number"));

        var departureAirport = new Airport();
        departureAirport.setId(resultSet.getLong("departure_airport_id"));
        departureAirport.setCode(resultSet.getString("departure_airport_code"));
        departureAirport.setName(resultSet.getString("departure_airport_name"));
        departureAirport.setAddress(departureAddress);

        var arrivalAddress = new Address();
        departureAddress.setId(resultSet.getLong("arrival_address_id"));
        departureAddress.setCountry(resultSet.getString("arrival_address_country"));
        departureAddress.setCity(resultSet.getString("arrival_address_city"));
        departureAddress.setStreet(resultSet.getString("arrival_address_street"));
        departureAddress.setHouseNumber(resultSet.getInt("arrival_address_house_number"));

        var arrivalAirport = new Airport();
        arrivalAirport.setId(resultSet.getLong("arrival_airport_id"));
        arrivalAirport.setCode(resultSet.getString("arrival_airport_code"));
        arrivalAirport.setName(resultSet.getString("arrival_airport_name"));
        arrivalAirport.setAddress(arrivalAddress);

        var flight = new Flight();
        flight.setId(resultSet.getLong("flight_id"));
        flight.setAllSeats(resultSet.getInt("all_seats"));
        flight.setFreeSeats(resultSet.getInt("free_seats"));
        flight.setDepartureDate(resultSet.getObject("departure_date", LocalDateTime.class));
        flight.setArrivalDate(resultSet.getObject("arrival_date", LocalDateTime.class));
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);

        var passport = passportResultSetMapper.map(resultSet)
                .orElseThrow(() -> new MappingException("Passport mapping error"));

        var passenger = new Passenger();
        passenger.setId(resultSet.getLong("passenger_id"));
        passenger.setFirstName(resultSet.getString("first_name"));
        passenger.setLastName(resultSet.getString("last_name"));
        passenger.setFatherName(resultSet.getString("father_name"));
        passenger.setMale(resultSet.getBoolean("male"));
        passenger.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
        passenger.setUserId(resultSet.getLong("user_id"));
        passenger.setPassport(passport);

        var ticket = new Ticket();
        ticket.setId(resultSet.getLong("ticket_id"));
        ticket.setTicketNumber(resultSet.getLong("ticket_number"));
        ticket.setTicketStatus(TicketStatus.valueOf(resultSet.getString("ticket_status")));
        ticket.setServiceClass(ServiceClass.valueOf(resultSet.getString("service_class")));
        ticket.setSeatNumber(resultSet.getInt("seat_number"));
        ticket.setPurchaseDate(resultSet.getObject("purchase_date", LocalDateTime.class));
        ticket.setUpdatedDate(resultSet.getObject("updated_date", LocalDateTime.class));
        ticket.setBaggageWeight(resultSet.getDouble("baggage_weight"));
        ticket.setCarryOnBaggageWeight(resultSet.getDouble("carry_on_baggage_weight"));
        ticket.setFlight(flight);
        ticket.setPassenger(passenger);

        return Optional.of(ticket);
    }
}
