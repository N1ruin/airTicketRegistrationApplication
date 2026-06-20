package mapper;

import domain.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Optional;

public class TicketResultSetMapper implements ResultSetMapper<Ticket> {
    @Override
    public Optional<Ticket> mapRow(ResultSet resultSet) throws SQLException {
        var ticket = new Ticket();
        ticket.setId(resultSet.getLong("ticket_id"));
        ticket.setTicketNumber(resultSet.getLong("ticket_number"));
        ticket.setTicketStatus(TicketStatus.valueOf(resultSet.getString("ticket_status")));
        ticket.setTicketRank(TicketRank.valueOf(resultSet.getString("ticket_rank")));
        ticket.setSeatNumber(resultSet.getInt("seat_number"));
        ticket.setPurchaseDate(resultSet.getObject("purchase_date", ZonedDateTime.class));
        ticket.setUpdatedDate(resultSet.getObject("updated_date", ZonedDateTime.class));
        ticket.setBaggageWeight(resultSet.getDouble("baggage_weight"));
        ticket.setCarryOnBaggageWeight(resultSet.getDouble("carry_on_weight"));
        ticket.setFlightId(resultSet.getLong("flight_id"));
        ticket.setPassengerId(resultSet.getLong("passenger_id"));

        return Optional.of(ticket);
    }
}
