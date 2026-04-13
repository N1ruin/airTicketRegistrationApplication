package converter.passenger;

import domain.Passenger;
import dto.passenger.UpdatePassengerResponse;
import converter.Converter;
import converter.passsport.PassportDtoConverter;

public class UpdatePassengerResponseConverter implements Converter<Passenger, UpdatePassengerResponse> {
    private final PassportDtoConverter passportDtoConverter;

    public UpdatePassengerResponseConverter(PassportDtoConverter passportDtoConverter) {
        this.passportDtoConverter = passportDtoConverter;
    }

    @Override
    public UpdatePassengerResponse convert(Passenger passenger) {
        var passportDto = passportDtoConverter.convert(passenger.getPassport());

        return new UpdatePassengerResponse(passenger.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getFatherName(), passenger.isMale(), passenger.getBirthDate(), passportDto);
    }
}
