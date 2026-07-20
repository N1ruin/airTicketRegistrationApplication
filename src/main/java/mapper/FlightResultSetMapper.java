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
        flight.setId(resultSet.getLong("id"));
        flight.setSeatsCount(resultSet.getInt("seats_count"));
        flight.setFreeSeats(resultSet.getInt("free_seats"));
        flight.setDepartureDate(resultSet.getObject("departure_date", ZonedDateTime.class));
        flight.setArrivalDate(resultSet.getObject("arrival_date", ZonedDateTime.class));
        flight.setDepartureAirportId(resultSet.getString("departure_airport_id"));
        flight.setArrivalAirportId(resultSet.getString("arrival_airport_id"));

        return Optional.of(flight);
    }
}
