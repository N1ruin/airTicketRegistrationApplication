package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.Converter;

public class AirportConverter implements Converter<Airport, AirportDto> {
    @Override
    public AirportDto convert(Airport airport) {
        var addressId = airport.getAddress().getId();

        return new AirportDto(airport.getCode(), airport.getName(), addressId);
    }
}
