package unit;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.RequestParameterExtractor;

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

        var result = requestParameterExtractor.extractId(request, true);

        assertEquals(1L, result);
    }

    @Test
    void extractIdFailureParamIsNull() {
        when(request.getParameter("id")).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> requestParameterExtractor.extractId(request, true));
    }

    @Test
    void extractIdSuccessWhenNotRequiredAndParamIsNull() {
        when(request.getParameter("id")).thenReturn(null);

        var result = requestParameterExtractor.extractId(request, false);

        assertNull(result);
    }

    @Test
    void extractIdSuccessWhenNotRequiredAndParamIsBlank() {
        when(request.getParameter("id")).thenReturn("");

        var result = requestParameterExtractor.extractId(request, false);

        assertNull(result);
    }

    @Test
    void extractIdFailureNumberFormatException() {
        when(request.getParameter("id")).thenReturn("asda");

        assertThrows(ValidationException.class,
                () -> requestParameterExtractor.extractId(request, true));
    }
}
