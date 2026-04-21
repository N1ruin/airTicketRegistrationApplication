package unit.service;

import domain.Address;
import exception.EntityAlreadyExistException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.AddressRepository;
import repository.TransactionHelper;
import service.AddressService;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private TransactionHelper transactionHelper;
    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void saveSuccess() {
        var address = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address)).thenReturn(Optional.empty());
        when(addressRepository.save(address)).thenAnswer(invocation -> {
            Address addres = invocation.getArgument(0);
            addres.setId(1L);
            return addres;
        });

        var result = addressService.save(address);

        assertNotNull(result);
        assertEquals(1L, address.getId());
        assertEquals("Country", result.getCountry());
        assertEquals("City", result.getCity());
        assertEquals("Street", result.getStreet());
        assertEquals(1, result.getHouseNumber());
        verify(addressRepository).findByCountryAndCityAndStreetAndHouseNumber(address);
        verify(addressRepository).save(address);
    }

    @Test
    void saveThrowEntityAlreadyExist() {
        var address = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address)).thenReturn(Optional.of(new Address()));

        assertThrows(EntityAlreadyExistException.class, () -> addressService.save(address));
        verify(addressRepository).findByCountryAndCityAndStreetAndHouseNumber(address);
    }

    @Test
    void updateSuccess() {
        var airportId = 1L;
        var newAddressData = getAddress();
        var existingAddress = new Address();
        existingAddress.setId(100L);
        existingAddress.setStreet("Old Street");
        when(addressRepository.findByAirportId(airportId)).thenReturn(Optional.of(existingAddress));
        when(addressRepository.update(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = addressService.update(newAddressData, airportId);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Country", result.getCountry());
        assertEquals("City", result.getCity());
        assertEquals("Street", result.getStreet());
        assertEquals(1, result.getHouseNumber());
        verify(addressRepository).findByAirportId(airportId);
        verify(addressRepository).update(existingAddress);
    }

    @Test
    void updateAddressExistThrowAlreadyExistException() {
        var airportId = 1L;
        var address = getAddress();
        when(addressRepository.findByAirportId(airportId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> addressService.update(address, airportId));
    }

    private Address getAddress() {
        var address = new Address();
        address.setCountry("Country");
        address.setCity("City");
        address.setStreet("Street");
        address.setHouseNumber(1);
        return address;
    }
}
