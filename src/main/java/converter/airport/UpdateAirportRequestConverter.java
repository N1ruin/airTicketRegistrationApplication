package converter.airport;

import domain.Airport;
import dto.airport.UpdateAirportRequest;
import converter.Converter;

public class UpdateAirportRequestConverter implements Converter<UpdateAirportRequest, Airport> {
    @Override
    public Airport convert(UpdateAirportRequest request) {
        var airport = new Airport();

        airport.setId(request.id());
        airport.setName(request.name());
        airport.setActive(request.isActive());
        airport.setCountry(request.country());
        airport.setCity(request.city());
        airport.setStreet(request.street());
        airport.setHouseNumber(request.houseNumber());

        return airport;
    }
}
