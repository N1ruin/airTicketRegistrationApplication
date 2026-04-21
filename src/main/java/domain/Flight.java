package domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Flight {
    private Long id;
    private Integer allSeats;
    private Integer freeSeats;
    private Airport departureAirport;
    private Airport arrivalAirport;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAllSeats() {
        return allSeats;
    }

    public void setAllSeats(Integer allSeats) {
        this.allSeats = allSeats;
    }

    public Integer getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(Integer freeSeats) {
        this.freeSeats = freeSeats;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
