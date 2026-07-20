package domain;

import java.time.ZonedDateTime;

public class Ticket {
    private Long id;
    private long ticketNumber;
    private TicketStatus ticketStatus;
    private TicketRank ticketRank;
    private int seatNumber;
    private long flightId;
    private long passengerId;
    private ZonedDateTime purchaseDate;
    private ZonedDateTime updatedDate;
    private double baggageWeight;
    private double carryOnBaggageWeight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public TicketRank getTicketRank() {
        return ticketRank;
    }

    public void setTicketRank(TicketRank ticketRank) {
        this.ticketRank = ticketRank;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getBaggageWeight() {
        return baggageWeight;
    }

    public void setBaggageWeight(double baggageWeight) {
        this.baggageWeight = baggageWeight;
    }

    public double getCarryOnBaggageWeight() {
        return carryOnBaggageWeight;
    }

    public void setCarryOnBaggageWeight(double carryOnBaggageWeight) {
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
