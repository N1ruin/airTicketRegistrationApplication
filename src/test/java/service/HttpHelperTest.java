package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpHelperTest {
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @InjectMocks
    private HttpHelper httpHelper;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    void getRequestBodySuccess() throws IOException {
        var user = new User();
        var mockInputStream = mock(ServletInputStream.class);
        when(httpServletRequest.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(User.class))).thenReturn(user);

        var result = httpHelper.getRequestBody(httpServletRequest, User.class);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getRequestBodyFailureThrowIOException() throws IOException {
        var mockInputStream = mock(ServletInputStream.class);
        when(httpServletRequest.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(User.class))).thenThrow(IOException.class);

        assertThrows(IOException.class, () -> httpHelper.getRequestBody(httpServletRequest, User.class));
    }

    @Test
    void writeResponseBodySuccess() throws IOException {
        var user = new User();
        var expectedBytes = "{}".getBytes();
        var mockOutputStream = mock(ServletOutputStream.class);
        when(httpServletResponse.getOutputStream()).thenReturn(mockOutputStream);
        when(objectMapper.writeValueAsBytes(any(User.class))).thenReturn(expectedBytes);

        httpHelper.writeResponseBody(httpServletResponse, user);

        verify(mockOutputStream).write(expectedBytes);
    }

    @Test
    void writeResponseBodyFailureThrowIOException() throws IOException {
        var user = new User();
        var mockOutputStream = mock(ServletOutputStream.class);
        when(httpServletResponse.getOutputStream()).thenReturn(mockOutputStream);
        when(objectMapper.writeValueAsBytes(any(User.class))).thenThrow(new RuntimeException());

        assertDoesNotThrow(() -> httpHelper.writeResponseBody(httpServletResponse, user));
    }
}
