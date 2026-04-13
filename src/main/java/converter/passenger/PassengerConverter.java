package converter.passenger;

import domain.Passenger;
import dto.passenger.PassengerDto;
import converter.Converter;
import converter.passsport.PassportConverter;

public class PassengerConverter implements Converter<PassengerDto, Passenger> {
    private final PassportConverter passportConverter;

    public PassengerConverter(PassportConverter passportConverter) {
        this.passportConverter = passportConverter;
    }

    @Override
    public Passenger convert(PassengerDto passengerDto) {
        var passenger = new Passenger();

        passenger.setFirstName(passengerDto.firstName());
        passenger.setLastName(passengerDto.lastName());
        passenger.setFatherName(passengerDto.fatherName());
        passenger.setMale(passengerDto.male());
        passenger.setBirthDate(passengerDto.birthDate());
        passenger.setUserId(passengerDto.id());

        var passport = passportConverter.convert(passengerDto.passportDto());
        passenger.setPassport(passport);

        return passenger;
    }
}
