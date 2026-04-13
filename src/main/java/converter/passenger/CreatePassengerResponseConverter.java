package converter.passenger;

import domain.Passenger;
import dto.passenger.CreatePassengerResponse;
import dto.passport.PassportDto;
import converter.Converter;

public class CreatePassengerResponseConverter implements Converter<Passenger, CreatePassengerResponse> {
    @Override
    public CreatePassengerResponse convert(Passenger passenger) {
        var passport = passenger.getPassport();
        var passportDto = new PassportDto(passport.getSeries(), passport.getNumber(), passport.getCitizenship(),
                passport.getIssueDate(), passport.getExpiredDate());

        return new CreatePassengerResponse(passenger.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getFatherName(), passenger.isMale(), passenger.getBirthDate(), passportDto);
    }
}
