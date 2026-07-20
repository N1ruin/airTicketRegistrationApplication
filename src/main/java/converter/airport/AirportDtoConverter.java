package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.Converter;

public class AirportDtoConverter implements Converter<AirportDto, Airport> {
    @Override
    public Airport convert(AirportDto dto) {
        var airport = new Airport();

        airport.setId(dto.code());
        airport.setName(dto.name());
        airport.setCountry(dto.country());
        airport.setCity(dto.city());
        airport.setStreet(dto.street());
        airport.setHouseNumber(dto.houseNumber());

        return airport;
    }
}
