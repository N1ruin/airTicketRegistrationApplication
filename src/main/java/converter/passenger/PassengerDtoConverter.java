package converter.passenger;

import domain.Passenger;
import dto.passenger.PassengerDto;
import converter.ListConverter;
import converter.Converter;
import converter.passsport.PassportDtoConverter;

import java.util.List;

public class PassengerDtoConverter implements Converter<Passenger, PassengerDto>, ListConverter<Passenger, PassengerDto> {
    private final PassportDtoConverter passportDtoConverter;

    public PassengerDtoConverter(PassportDtoConverter passportDtoConverter) {
        this.passportDtoConverter = passportDtoConverter;
    }

    @Override
    public PassengerDto convert(Passenger passenger) {
        var passportDto = passportDtoConverter.convert(passenger.getPassport());

        return new PassengerDto(passenger.getId(), passenger.getFirstName(), passenger.getLastName(),
                passenger.getFatherName(), passenger.isMale(), passenger.getBirthDate(), passportDto,
                passenger.getUserId());
    }

    @Override
    public List<PassengerDto> convertAll(List<Passenger> passengers) {
        return passengers.stream()
                .map(this::convert)
                .toList();
    }
}
