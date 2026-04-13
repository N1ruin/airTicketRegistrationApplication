package converter;

import java.util.List;

public interface ListConverter<S, R> {
    List<R> convertAll(List<S> sourceList);
}
