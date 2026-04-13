package dto.airport;

import dto.address.AddressDto;

public record UpdateAirportRequest(Long id, String code, String name, AddressDto addressDto) {
}
