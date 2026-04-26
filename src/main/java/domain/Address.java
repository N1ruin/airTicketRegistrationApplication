package domain;

import jakarta.persistence.*;

@Entity
@Table(name = "address", schema = "tickets_application",
        uniqueConstraints = @UniqueConstraint(name = "unique_address",
                columnNames = {"country", "city", "street", "house_number"}))
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 70, nullable = false)
    private String country;
    @Column(length = 100, nullable = false)
    private String city;
    @Column(length = 100)
    private String street;
    @Column(name = "house_number")
    private Integer houseNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }
}
