package domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticket_number")
    private Long ticketNumber;
    @Column(name = "ticket_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;
    @Column(name = "service_class")
    @Enumerated(EnumType.STRING)
    private ServiceClass serviceClass;
    @Column(name = "seat_number")
    private Integer seatNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private Flight flight;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @Column(name = "baggage_weight")
    private Double baggageWeight;
    @Column(name = "carry_on_weight")
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

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
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

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    @PrePersist
    private void onPersist() {
        this.purchaseDate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdated() {
        this.updatedDate = LocalDateTime.now();
    }
}
