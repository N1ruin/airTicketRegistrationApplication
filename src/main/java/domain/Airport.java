package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "airport", schema = "tickets_application")
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10, nullable = false, unique = true)
    private String code;
    @Column(length = 100, nullable = false)
    private String name;
    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    @Column(name = "status", length = 20, nullable = false)
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
}
