package converter.airport;

import domain.Airport;
import dto.airport.AirportDto;
import converter.Converter;
import converter.address.AddressDtoConverter;

public class AirportDtoConverter implements Converter<AirportDto, Airport> {
    private final AddressDtoConverter addressDtoConverter;

    public AirportDtoConverter(AddressDtoConverter addressDtoConverter) {
        this.addressDtoConverter = addressDtoConverter;
    }

    @Override
    public Airport convert(AirportDto dto) {
        var airport = new Airport();

        airport.setName(dto.name());
        airport.setCode(dto.code());
        var address = addressDtoConverter.convert(dto.address());
        airport.setAddress(address);

        return airport;
    }
}
