package converter.airport;

import domain.Airport;
import dto.airport.UpdateAirportResponse;
import converter.address.AddressConverter;
import converter.Converter;

public class UpdateAirportResponseConverter implements Converter<Airport, UpdateAirportResponse> {
    private final AddressConverter addressConverter;

    public UpdateAirportResponseConverter(AddressConverter addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public UpdateAirportResponse convert(Airport airport) {
        var addressDto = addressConverter.convert(airport.getAddress());
        return new UpdateAirportResponse(airport.getId(), airport.getCode(), airport.getName(), addressDto);
    }
}
