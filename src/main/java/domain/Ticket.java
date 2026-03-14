package domain;

import java.time.LocalDateTime;

public class Ticket {
    private Long id;
    private ServiceClass serviceClass;
    private Integer seatNumber;
    private Flight flight;
    private Long flightId;
    private Passenger passenger;
    private Long passengerId;
    private LocalDateTime purchaseDate;
    private Double baggageWeight;
    private Double carryOnBaggageWeight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getBaggageWeight() {
        return baggageWeight;
    }

    public void setBaggageWeight(Double baggageWeight) {
        this.baggageWeight = baggageWeight;
    }

    public Double getCarryOnBaggageWeight() {
        return carryOnBaggageWeight;
    }

    public void setCarryOnBaggageWeight(Double carryOnBaggageWeight) {
        this.carryOnBaggageWeight = carryOnBaggageWeight;
    }
}
