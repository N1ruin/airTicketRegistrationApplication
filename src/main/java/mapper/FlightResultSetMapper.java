package mapper;

import domain.Flight;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Optional;

public class FlightResultSetMapper implements ResultSetMapper<Flight> {
    @Override
    public Optional<Flight> mapRow(ResultSet resultSet) throws SQLException {
        var flight = new Flight();
        flight.setId(resultSet.getLong("flight_id"));
        flight.setAllSeats(resultSet.getInt("all_seats"));
        flight.setFreeSeats(resultSet.getInt("free_seats"));
        flight.setDepartureDate(resultSet.getObject("departure_date", ZonedDateTime.class));
        flight.setArrivalDate(resultSet.getObject("arrival_date", ZonedDateTime.class));
        flight.setDepartureAirportCode(resultSet.getString("departure_airport_code"));
        flight.setArrivalAirportCode(resultSet.getString("arrival_airport_code"));

        return Optional.of(flight);
    }
}
