package validation;

import dto.address.AddressDto;

import java.util.List;

public class AddressValidationService {
    private static final String COUNTRY_PATTERN = "^[a-zA-Z\\s-]{1,70}$";
    private static final String CITY_PATTERN = "^[a-zA-Z0-9\\s-]{1,100}$";
    private static final String STREET_PATTERN = "^[a-zA-Zа-яА-ЯёЁ0-9\\s-.,]{1,100}$";

    public void validate(AddressDto addressDto, List<String> errors) {
        validateCountry(addressDto.country(), errors);
        validateCity(addressDto.city(), errors);
        validateStreet(addressDto.street(), errors);
        validateHouseNumber(addressDto.houseNumber(), errors);
    }

    private void validateCountry(String country, List<String> errors) {
        if (country == null || !country.matches(COUNTRY_PATTERN)) {
            errors.add("Country should contain only letters, spaces, or hyphens.");
        }
    }

    private void validateCity(String city, List<String> errors) {
        if (city == null || !city.matches(CITY_PATTERN)) {
            errors.add("City should contain letters, numbers, spaces, or hyphens.");
        }
    }

    private void validateStreet(String street, List<String> errors) {
        if (street == null || !street.matches(STREET_PATTERN)) {
            errors.add("Street should contain letters, numbers, spaces, dots, comma or hyphens.");
        }
    }

    private void validateHouseNumber(Integer houseNumber, List<String> errors) {
        if (houseNumber == null) {
            errors.add("House number cannot be null");
            return;
        }
        if (houseNumber <= 0) {
            errors.add("The house %d number cannot be negative.".formatted(houseNumber));
        }
    }
}
