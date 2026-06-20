package converter.passenger;

import converter.Converter;
import domain.Passenger;
import dto.passenger.PassengerDto;

public class PassengerDtoConverter implements Converter<Passenger, PassengerDto> {
    @Override
    public PassengerDto convert(Passenger passenger) {
        var passportId = passenger.getPassport().getId();

        return new PassengerDto(passenger.getId(),
                passportId,
                passenger.getUserId());
    }
}
