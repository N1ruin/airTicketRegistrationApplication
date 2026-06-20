package domain;

import java.util.ArrayList;
import java.util.List;

public class Passenger {
    private Long id;
    private Passport passport;
    private final List<Airport> favoriteAirports = new ArrayList<>();
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
