package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "passenger_favorite_airports")
public class PassengerFavoriteAirport {
    @EmbeddedId
    private PassengerFavoriteAirportId passengerFavoriteAirportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    @MapsId("passengerId")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airport_id")
    @MapsId("airportId")
    private Airport airport;

    @Column(name = "flights_count")
    private int flightCounts;

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    public int getFlightCounts() {
        return flightCounts;
    }

    public void setFlightCounts(int flightCounts) {
        this.flightCounts = flightCounts;
    }
}
