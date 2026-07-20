package unit.converter.address;

import dto.address.AddressDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressDtoConverterTest {
    private final AddressDtoConverter converter = new AddressDtoConverter();

    @Test
    void convertAddressDtoToAddressSuccess() {
        var country = "Российская Федерация";
        var city = "Химки";
        var street = "Шереметьевское шоссе";
        var houseNumber = 37;
        var dto = new AddressDto(country, city, street, houseNumber);

        var result = converter.convert(dto);

        assertNotNull(result);
        assertEquals("Российская Федерация", result.getCountry());
        assertEquals("Химки", result.getCity());
        assertEquals("Шереметьевское шоссе", result.getStreet());
        assertEquals(37, result.getHouseNumber());
    }
}
