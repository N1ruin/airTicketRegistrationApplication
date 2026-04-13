package dto.airport;

import dto.address.AddressDto;

public record UpdateAirportResponse(Long id, String code, String name, AddressDto addressDto) {
}
