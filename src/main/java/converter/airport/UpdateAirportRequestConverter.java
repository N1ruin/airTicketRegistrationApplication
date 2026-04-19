package converter.airport;

import domain.Airport;
import dto.airport.UpdateAirportRequest;
import converter.address.AddressDtoConverter;
import converter.Converter;

public class UpdateAirportRequestConverter implements Converter<UpdateAirportRequest, Airport> {
    private final AddressDtoConverter addressDtoConverter;

    public UpdateAirportRequestConverter(AddressDtoConverter addressDtoConverter) {
        this.addressDtoConverter = addressDtoConverter;
    }

    @Override
    public Airport convert(UpdateAirportRequest request) {
        var airport = new Airport();

        airport.setId(request.id());
        airport.setName(request.name());
        airport.setCode(request.code());

        var address = addressDtoConverter.convert(request.addressDto());
        airport.setAddress(address);

        return airport;
    }
}
