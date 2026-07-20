package converter;

import java.util.List;

public interface Converter<S, R> {
    R convert(S source);

    default List<R> convertAll(List<S> sourceList) {
        return sourceList.stream()
                .map(this::convert)
                .toList();
    }
}
