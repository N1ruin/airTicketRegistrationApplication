package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.ListConverter;
import converter.address.AddressConverter;
import converter.Converter;

import java.util.List;

public class AirportConverter implements Converter<Airport, AirportDto>, ListConverter<Airport, AirportDto> {
    private final AddressConverter addressConverter;

    public AirportConverter(AddressConverter addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public AirportDto convert(Airport airport) {
        var addressDto = addressConverter.convert(airport.getAddress());
        return new AirportDto(airport.getId(), airport.getCode(), airport.getName(), addressDto);
    }

    @Override
    public List<AirportDto> convertAll(List<Airport> airports) {
        return airports.stream()
                .map(this::convert)
                .toList();
    }
}
