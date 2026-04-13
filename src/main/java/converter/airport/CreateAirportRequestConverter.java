package converter.airport;

import domain.Airport;
import dto.airport.CreateAirportRequest;
import converter.address.AddressDtoConverter;
import converter.Converter;

public class CreateAirportRequestConverter implements Converter<CreateAirportRequest, Airport> {
    private final AddressDtoConverter addressDtoConverter;

    public CreateAirportRequestConverter(AddressDtoConverter addressDtoConverter) {
        this.addressDtoConverter = addressDtoConverter;
    }

    @Override
    public Airport convert(CreateAirportRequest createAirportRequest) {
        var airport = new Airport();

        airport.setName(createAirportRequest.name());
        airport.setCode(createAirportRequest.code());

        var address = addressDtoConverter.convert(createAirportRequest.addressDto());
        airport.setAddress(address);

        return airport;
    }
}
