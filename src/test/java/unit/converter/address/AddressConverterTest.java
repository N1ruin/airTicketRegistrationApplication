package unit.converter.address;

import converter.address.AddressConverter;
import domain.Address;
import dto.address.AddressDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressConverterTest {
    private final AddressConverter converter = new AddressConverter();

    @Test
    void convertAddressToAddressDtoSuccess() {
        Address address = new Address();
        address.setCountry("Российская Федерация");
        address.setCity("Химки");
        address.setStreet("Шереметьевское шоссе");
        address.setHouseNumber(37);

        var result = converter.convert(address);

        assertNotNull(result);
        assertEquals("Российская Федерация", result.country());
        assertEquals("Химки", result.city());
        assertEquals("Шереметьевское шоссе", result.street());
        assertEquals(37, result.houseNumber());
    }
}
