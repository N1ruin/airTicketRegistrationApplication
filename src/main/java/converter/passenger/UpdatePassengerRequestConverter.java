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

        var passport = passportConverter.convert(request.passportDto());
        passenger.setPassport(passport);

        return passenger;
    }
}
