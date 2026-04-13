package unit;

import exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import servlet.RequestParameterExtractor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestParameterExtractorTest {
    @Mock
    private HttpServletRequest request;

    private final RequestParameterExtractor requestParameterExtractor = new RequestParameterExtractor();


    @Test
    void extractIdSuccess() {
        when(request.getParameter("id")).thenReturn("1");

        var result = requestParameterExtractor.extractId(request);

        assertEquals(1L, result);
    }

    @Test
    void extractIdFailureParamIsNull() {
        when(request.getParameter("id")).thenReturn(null);

        var result = requestParameterExtractor.extractId(request);

        assertNull(result);
    }

    @Test
    void extractIdFailureParamIsBlank() {
        when(request.getParameter("id")).thenReturn("");

        var result = requestParameterExtractor.extractId(request);

        assertNull(result);
    }

    @Test
    void extractIdFailureNumberFormanException() {
        when(request.getParameter("id")).thenReturn("asda");

        assertThrows(ValidationException.class, () -> requestParameterExtractor.extractId(request));
    }

}
