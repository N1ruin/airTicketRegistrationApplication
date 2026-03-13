package by.niruin.ticketRegistrationApp.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Passenger {
    private Long id;
    private String firstName;
    private String lastName;
    private String fatherName;
    private boolean male;
    private LocalDate birthDate;
    private Passport passport;
    private List<Airport> favoriteAirports = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public List<Airport> getFavoriteAirports() {
        return favoriteAirports;
    }

    public void setFavoriteAirports(List<Airport> favoriteAirports) {
        this.favoriteAirports = favoriteAirports;
    }
}
