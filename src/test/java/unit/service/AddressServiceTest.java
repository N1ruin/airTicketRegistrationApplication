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
import util.TransactionHelper;
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
    void createSuccess() {
        var address = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address)).thenReturn(Optional.empty());
        when(addressRepository.create(address)).thenAnswer(invocation -> {
            Address addres = invocation.getArgument(0);
            addres.setId(1L);
            return addres;
        });

        var result = addressService.create(address);

        assertNotNull(result);
        assertEquals(1L, address.getId());
        assertEquals("Country", result.getCountry());
        assertEquals("City", result.getCity());
        assertEquals("Street", result.getStreet());
        assertEquals(1, result.getHouseNumber());
        verify(addressRepository).findByCountryAndCityAndStreetAndHouseNumber(address);
        verify(addressRepository).create(address);
    }

    @Test
    void createThrowEntityAlreadyExist() {
        var address = getAddress();
        when(addressRepository.findByCountryAndCityAndStreetAndHouseNumber(address))
                .thenReturn(Optional.of(new Address()));

        assertThrows(EntityAlreadyExistException.class, () -> addressService.create(address));
        verify(addressRepository).findByCountryAndCityAndStreetAndHouseNumber(address);
        verify(addressRepository, never()).create(any());
    }

    @Test
    void updateSuccess() {
        var existingAddress = getAddress();
        existingAddress.setId(100L);
        existingAddress.setStreet("Old Street");

        var newAddressData = getAddress();
        newAddressData.setId(100L);
        newAddressData.setStreet("New Street");

        when(addressRepository.findById(existingAddress.getId())).thenReturn(Optional.of(existingAddress));
        when(addressRepository.update(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = addressService.update(newAddressData);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Country", result.getCountry());
        assertEquals("City", result.getCity());
        assertEquals("New Street", result.getStreet());
        assertEquals(1, result.getHouseNumber());
        verify(addressRepository).findById(existingAddress.getId());
        verify(addressRepository).update(existingAddress);
    }

    @Test
    void updateAddressExistThrowAlreadyExistException() {
        var address = getAddress();
        address.setId(999L);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> addressService.update(address));

        verify(addressRepository).findById(address.getId());
        verify(addressRepository, never()).update(any());
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
