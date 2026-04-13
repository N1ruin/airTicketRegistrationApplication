package converter.airport;

import domain.Airport;
import dto.airport.CreateAirportResponse;
import converter.address.AddressConverter;
import converter.Converter;

public class CreateAirportResponseConverter implements Converter<Airport, CreateAirportResponse> {
    private final AddressConverter addressConverter;

    public CreateAirportResponseConverter(AddressConverter addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public CreateAirportResponse convert(Airport airport) {
        var addressDto = addressConverter.convert(airport.getAddress());
        return new CreateAirportResponse(airport.getId(), airport.getCode(), airport.getName(), addressDto);
    }
}
