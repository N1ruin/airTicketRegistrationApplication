package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.Converter;

public class AirportDtoConverter implements Converter<AirportDto, Airport> {
    @Override
    public Airport convert(AirportDto dto) {
        var airport = new Airport();

        airport.setName(dto.name());
        airport.setCode(dto.code());

        return airport;
    }
}
