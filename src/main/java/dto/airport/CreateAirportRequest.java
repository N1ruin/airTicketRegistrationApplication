package dto.airport;

import dto.address.AddressDto;

public record CreateAirportRequest(String code, String name, AddressDto addressDto) {
}
