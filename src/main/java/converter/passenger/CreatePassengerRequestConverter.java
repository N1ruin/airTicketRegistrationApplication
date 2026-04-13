package converter.passenger;

import domain.Passenger;
import dto.passenger.CreatePassengerRequest;
import converter.Converter;
import converter.passsport.PassportConverter;

public class CreatePassengerRequestConverter implements Converter<CreatePassengerRequest, Passenger> {
    private final PassportConverter passportConverter;

    public CreatePassengerRequestConverter(PassportConverter passportConverter) {
        this.passportConverter = passportConverter;
    }

    @Override
    public Passenger convert(CreatePassengerRequest request) {
        var passenger = new Passenger();

        passenger.setFirstName(request.firstName());
        passenger.setLastName(request.lastName());
        passenger.setFatherName(request.fatherName());
        passenger.setMale(request.male());
        passenger.setBirthDate(request.birthDate());

        var passport = passportConverter.convert(request.passportDto());
        passenger.setPassport(passport);

        return passenger;
    }
}
