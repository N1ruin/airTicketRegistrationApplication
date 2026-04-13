package converter.passenger;

import domain.Passenger;
import dto.passenger.UpdatePassengerRequest;
import converter.Converter;
import converter.passsport.PassportConverter;

public class UpdatePassengerRequestConverter implements Converter<UpdatePassengerRequest, Passenger> {
    private final PassportConverter passportConverter;

    public UpdatePassengerRequestConverter(PassportConverter passportConverter) {
        this.passportConverter = passportConverter;
    }

    @Override
    public Passenger convert(UpdatePassengerRequest request) {
        var passenger = new Passenger();

        passenger.setId(request.id());
        passenger.setFirstName(request.firstName());
        passenger.setLastName(request.lastName());
        passenger.setFatherName(request.fatherName());
        passenger.setMale(request.male());
        passenger.setBirthDate(request.birthDate());
        passenger.setUserId(request.userId());

        var passport = passportConverter.convert(request.passportDto());
        passenger.setPassport(passport);

        return passenger;
    }
}
