package converter.address;

import domain.Address;
import dto.address.AddressDto;
import converter.Converter;

public class AddressConverter implements Converter<Address, AddressDto> {
    @Override
    public AddressDto convert(Address address) {
        return new AddressDto(address.getCountry(), address.getCity(), address.getStreet(), address.getHouseNumber());
    }
}
