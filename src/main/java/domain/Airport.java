package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "airport")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String code;
    @Column
    private String name;
    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id")
    private Address address;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AirportStatus airportStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AirportStatus getAirportStatus() {
        return airportStatus;
    }

    public void setAirportStatus(AirportStatus airportStatus) {
        this.airportStatus = airportStatus;
    }

    @PrePersist
    protected void onCreate() {
        if (airportStatus == null) {
            airportStatus = AirportStatus.WORKS;
        }
    }
}
