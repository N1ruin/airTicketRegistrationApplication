package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHolder {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private static final Logger log = LogManager.getLogger(ConnectionHolder.class);

    private final DataSource dataSource;

    public ConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        var connection = connectionHolder.get();

        if (connection == null) {
            try {
                connection = dataSource.getConnection();
                connectionHolder.set(connection);
            } catch (SQLException e) {
                connectionHolder.remove();
                log.error("Connection getting failed", e);
                throw new RuntimeException("Connection getting failed", e);
            }
        } else {
            try {
                if (connection.isClosed()) {
                    connectionHolder.remove();
                    return getConnection();
                }
            } catch (SQLException e) {
                connectionHolder.remove();
                log.error("Connection status checking failed", e);
                throw new RuntimeException("Connection status checking failed", e);
            }
        }

        return connection;
    }

    public void clearConnection() {
        connectionHolder.remove();
    }
}
