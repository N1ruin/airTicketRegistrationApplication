package mapper;

import domain.Address;
import domain.Airport;
import domain.Flight;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightResultSetMapper implements ResultSetMapper<Optional<Flight>>, ResultSetListMapper<Flight> {

    @Override
    public Optional<Flight> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    @Override
    public List<Flight> mapList(ResultSet resultSet) throws SQLException {
        var flights = new ArrayList<Flight>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(flights::add);
        }
        return flights;
    }

    private Optional<Flight> mapRow(ResultSet resultSet) throws SQLException {
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

        return Optional.of(flight);
    }
}
