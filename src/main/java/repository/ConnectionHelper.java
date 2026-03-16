package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHelper {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final Logger log = LogManager.getLogger(ConnectionHelper.class);
    private final DataSource dataSource;

    public ConnectionHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {

        try {
            Connection connection = connectionHolder.get();
            if (connection == null || connection.isClosed()) {
                connection = dataSource.getConnection();

                connectionHolder.set(connection);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    public void closeConnection() {
        Connection connection = connectionHolder.get();

        try {
            if (connection != null || !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("SQL error with closing connection", e);
            throw new RuntimeException(e);
        } finally {
            connectionHolder.remove();
        }
    }
}
