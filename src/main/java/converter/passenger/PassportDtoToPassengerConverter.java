package converter.passenger;

import domain.Passenger;
import converter.Converter;
import converter.passsport.PassportConverter;
import dto.passport.PassportDto;

public class PassportDtoToPassengerConverter implements Converter<PassportDto, Passenger> {
    private final PassportConverter passportConverter;

    public PassportDtoToPassengerConverter(PassportConverter passportConverter) {
        this.passportConverter = passportConverter;
    }

    @Override
    public Passenger convert(PassportDto dto) {
        var passenger = new Passenger();

        var passport = passportConverter.convert(dto);
        passenger.setPassport(passport);

        return passenger;
    }
}
