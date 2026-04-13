package converter.address;

import domain.Address;
import dto.address.AddressDto;
import converter.Converter;

public class AddressDtoConverter implements Converter<AddressDto, Address> {
    @Override
    public Address convert(AddressDto addressDto) {
        var address = new Address();

        address.setCountry(addressDto.country());
        address.setCity(addressDto.city());
        address.setStreet(addressDto.street());
        address.setHouseNumber(addressDto.houseNumber());

        return address;
    }
}
