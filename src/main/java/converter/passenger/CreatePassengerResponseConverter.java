package converter.passenger;

import converter.passsport.PassportDtoConverter;
import domain.Passenger;
import dto.passenger.CreatePassengerResponse;
import converter.Converter;

public class CreatePassengerResponseConverter implements Converter<Passenger, CreatePassengerResponse> {
    private final PassportDtoConverter passportDtoConverter;

    public CreatePassengerResponseConverter(PassportDtoConverter passportDtoConverter) {
        this.passportDtoConverter = passportDtoConverter;
    }

    @Override
    public CreatePassengerResponse convert(Passenger passenger) {
        var passport = passenger.getPassport();

        var passportDto = passportDtoConverter.convert(passport);

        return new CreatePassengerResponse(passenger.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getFatherName(), passenger.isMale(), passenger.getBirthDate(), passportDto,
                passenger.getUser());
    }
}
