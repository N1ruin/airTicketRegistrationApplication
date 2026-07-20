package converter.passenger;

import converter.Converter;
import domain.Passenger;
import dto.passenger.PassengerDto;

public class PassengerDtoConverter implements Converter<Passenger, PassengerDto> {
    @Override
    public PassengerDto convert(Passenger passenger) {
        return new PassengerDto(
                passenger.getId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getFatherName(),
                passenger.isMale(),
                passenger.getPassportSeries(),
                passenger.getPassportNumber(),
                passenger.getCitizenship(),
                passenger.getBirthDate(),
                passenger.getPassportIssueDate(),
                passenger.getPassportExpiredDate(),
                passenger.getUserId());
    }
}
