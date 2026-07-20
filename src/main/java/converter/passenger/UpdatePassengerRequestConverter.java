package converter.passenger;

import converter.Converter;
import domain.Passenger;
import dto.passenger.UpdatePassengerRequest;

public class UpdatePassengerRequestConverter implements Converter<UpdatePassengerRequest, Passenger> {
    @Override
    public Passenger convert(UpdatePassengerRequest request) {
        var passenger = new Passenger();

        passenger.setId(request.id());
        passenger.setFirstName(request.firstName());
        passenger.setLastName(request.lastName());
        passenger.setFatherName(request.fatherName());
        passenger.setMale(request.isMale());
        passenger.setBirthDate(request.birthDate());
        passenger.setPassportSeries(request.passportSeries());
        passenger.setPassportNumber(request.passportNumber());
        passenger.setCitizenship(request.citizenship());
        passenger.setPassportIssueDate(request.passportIssueDate());
        passenger.setPassportExpiredDate(request.passportExpiredDate());

        return passenger;
    }
}
