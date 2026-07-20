package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Passenger {
    private Long id;
    private String passportSeries;
    private String passportNumber;
    private String firstName;
    private String lastName;
    private String fatherName;
    private boolean isMale;
    private String citizenship;
    private LocalDate birthDate;
    private LocalDate passportIssueDate;
    private LocalDate passportExpiredDate;
    private final List<Airport> favoriteAirports = new ArrayList<>();
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
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

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(LocalDate passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public LocalDate getPassportExpiredDate() {
        return passportExpiredDate;
    }

    public void setPassportExpiredDate(LocalDate passportExpiredDate) {
        this.passportExpiredDate = passportExpiredDate;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
