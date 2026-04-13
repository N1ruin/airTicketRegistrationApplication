package mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ResultSetListMapper<T> {
    List<T> mapList(ResultSet resultSet) throws SQLException;
}
