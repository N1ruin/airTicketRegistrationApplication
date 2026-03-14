package mapper.user;

public interface Mapper<S, R> {
    R map(S source);
}
