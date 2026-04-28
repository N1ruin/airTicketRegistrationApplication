package domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record PassengerFavoriteAirportId(Long airportId, Long passengerId) {
}
