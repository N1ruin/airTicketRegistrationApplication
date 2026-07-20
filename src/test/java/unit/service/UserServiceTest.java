package unit.service;

import converter.user.UserDtoConverter;
import domain.Role;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import security.PasswordEncoder;
import service.UserService;
import util.CurrentUserHolder;
import util.TransactionHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionHelper transactionHelper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDtoConverter userDtoConverter;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        CurrentUserHolder.setCurrentUserId(1L);
        CurrentUserHolder.setCurrentUserRole(Role.USER);
        lenient().when(transactionHelper.executeInTransaction(any(Supplier.class)))
                .thenAnswer(invocationOnMock -> ((Supplier<?>) invocationOnMock.getArgument(0)).get());

        lenient().doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(transactionHelper).executeInTransaction(any(Runnable.class));
    }

    @Test
    void signUpSuccess() {
        var user = getUser();
        when(userRepository.create(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        var result = userService.signUp(user, Role.USER);

        assertNotNull(result);
        assertEquals("email", user.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertFalse(result.isBlocked());
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).create(user);
    }

    @Test
    void signUpEmailExistThrowsException() {
        var user = getUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> userService.signUp(user, Role.USER));

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void signUpAdminSuccess() {
        var user = getUser();
        when(userRepository.create(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        var result = userService.signUp(user, Role.ADMIN);

        assertNotNull(result);
        assertEquals("email", user.getEmail());
        assertEquals(Role.ADMIN, result.getRole());
        assertFalse(result.isBlocked());
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).create(user);
    }

    @Test
    void signUpAdminEmailExistThrowsException() {
        var user = getUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> userService.signUp(user, Role.ADMIN));

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void signInSuccess() {
        var user = getUser();
        user.setId(1L);
        user.setPasswordHash("passwordHash");
        var password = "testpassword";
        user.setRole(Role.USER);
        user.setBlocked(false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.verify(password, user.getPasswordHash())).thenReturn(true);
        when(userRepository.update(user)).thenReturn(user);

        var result = userService.signIn(user.getEmail(), password);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("email", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertFalse(result.isBlocked());
        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).verify(password, user.getPasswordHash());
        verify(userRepository).update(user);
    }

    @Test
    void signInEmailNotExistThrowsInvalidCredentialsException() {
        var user = getUser();
        user.setPasswordHash("passwordHash");
        var password = "testpassword";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.signIn(user.getEmail(), password));

        verify(userRepository).findByEmail(user.getEmail());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userDtoConverter);
    }

    @Test
    void signInInvalidPasswordThrowsInvalidCredentialsException() {
        var user = getUser();
        user.setPasswordHash("passwordHash");
        var password = "testpassword";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.verify(password, user.getPasswordHash())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.signIn(user.getEmail(), password));

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).verify(password, user.getPasswordHash());
        verifyNoInteractions(userDtoConverter);
    }

    @Test
    void findAllReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        var result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findAllReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        var result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void blockSuccess() {
        var user = getUser();
        user.setBlocked(false);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.block(user.getId());

        assertTrue(user.isBlocked());
        verify(userRepository).findById(user.getId());
        verify(userRepository).update(user);
    }

    @Test
    void blockThrowsUserNotFound() {
        var user = getUser();
        user.setBlocked(false);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.block(user.getId()));

        verify(userRepository).findById(user.getId());
        verify(userRepository, times(0)).update(user);

    }

    @Test
    void blockIfUserBlocked() {
        var user = getUser();
        user.setBlocked(true);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.block(user.getId());

        assertTrue(user.isBlocked());
        verify(userRepository).findById(user.getId());
        verify(userRepository, times(0)).update(user);
    }

    @Test
    void unblockSuccess() {
        var user = getUser();
        user.setBlocked(true);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.unblock(user.getId());

        assertFalse(user.isBlocked());
        verify(userRepository).findById(user.getId());
        verify(userRepository).update(user);
    }

    @Test
    void unblockThrowsUserNotFound() {
        var user = getUser();
        user.setBlocked(true);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.unblock(user.getId()));

        verify(userRepository).findById(user.getId());
        verify(userRepository, times(0)).update(user);

    }

    @Test
    void unblockIfUserNotBlocked() {
        var user = getUser();
        user.setBlocked(false);
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.unblock(user.getId());

        assertFalse(user.isBlocked());
        verify(userRepository).findById(user.getId());
        verify(userRepository, times(0)).update(user);
    }

    @Test
    void findByIdSuccess() {
        var user = getUser();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findByIdThrowsEntityNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void updateSuccess() {
        var user = getUser();
        user.setId(1L);
        user.setPasswordHash("oldHash");
        var newPassword = "newPassword123";
        var newHash = "newHash";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(newHash);
        when(userRepository.update(user)).thenReturn(user);

        var result = userService.update(newPassword);

        assertNotNull(result);
        assertEquals(newHash, result.getPasswordHash());
        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).update(user);
    }

    @Test
    void updateThrowsEntityNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update("newPass"));
        verify(userRepository).findById(1L);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).update(any());
    }

    private User getUser() {
        var user = new User();
        user.setEmail("email");

        return user;
    }
}
