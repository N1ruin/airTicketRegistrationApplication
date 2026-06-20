package domain;

import java.time.ZonedDateTime;

public class Ticket {
    private Long id;
    private Long ticketNumber;
    private TicketStatus ticketStatus;
    private TicketRank ticketRank;
    private Integer seatNumber;
    private Long flightId;
    private Long passengerId;
    private ZonedDateTime purchaseDate;
    private ZonedDateTime updatedDate;
    private Double baggageWeight;
    private Double carryOnBaggageWeight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(Long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public TicketRank getTicketRank() {
        return ticketRank;
    }

    public void setTicketRank(TicketRank ticketRank) {
        this.ticketRank = ticketRank;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
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

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
}
