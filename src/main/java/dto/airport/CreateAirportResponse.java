package dto.airport;

import dto.address.AddressDto;

public record CreateAirportResponse(Long id, String code, String name, AddressDto addressDto) {
}
