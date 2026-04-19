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
        var updatedAddress = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(updatedAddress))
                .thenReturn(Optional.empty());
        when(addressRepository.update(updatedAddress)).thenReturn(updatedAddress);

        var result = addressService.update(updatedAddress);

        assertNotNull(result);
        assertEquals("Country", result.getCountry());
        assertEquals("City", result.getCity());
        assertEquals("Street", result.getStreet());
        assertEquals(1, result.getHouseNumber());
        verify(addressRepository).findByCountryAndCityAndStreetAndHouseNumber(updatedAddress);
    }

    @Test
    void updateAddressExistThrowAlreadyExistException() {
        var updatedAddress = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(updatedAddress))
                .thenReturn(Optional.of(updatedAddress));

        assertThrows(EntityAlreadyExistException.class, () -> addressService.update(updatedAddress));
    }

    @Test
    void deleteSuccess() {
        var id = 1L;
        var address = getAddress();
        address.setId(id);
        when(addressRepository.findById(id)).thenReturn(Optional.of(new Address()));

        addressService.deleteById(id);

        verify(addressRepository).findById(id);
        verify(addressRepository).deleteById(id);
    }

    @Test
    void deleteThrowNotFoundException() {
        var id = 1L;
        var address = getAddress();
        address.setId(id);
        when(addressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> addressService.deleteById(id));

        verify(addressRepository).findById(id);
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
