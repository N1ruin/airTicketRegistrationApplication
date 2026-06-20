package mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ResultSetMapper<T> {
    default Optional<T> map(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? mapRow(resultSet) : Optional.empty();
    }

    Optional<T> mapRow(ResultSet resultSet) throws SQLException;

    default List<T> mapList(ResultSet resultSet) throws SQLException {
        var resultList = new ArrayList<T>();

        while (resultSet.next()) {
            mapRow(resultSet).ifPresent(resultList::add);
        }

        return resultList;
    }
}
