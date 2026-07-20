package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.Converter;

public class AirportConverter implements Converter<Airport, AirportDto> {
    @Override
    public AirportDto convert(Airport airport) {
        return new AirportDto(
                airport.getId(),
                airport.getName(),
                airport.getCountry(),
                airport.getCity(),
                airport.getStreet(),
                airport.getHouseNumber());
    }
}
