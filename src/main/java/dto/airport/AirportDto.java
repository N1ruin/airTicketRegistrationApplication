package dto.airport;

import dto.address.AddressDto;

public record AirportDto(Long id, String code, String name, AddressDto address) {
}
