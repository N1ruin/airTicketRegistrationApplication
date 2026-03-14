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
        log.info("Getting connection from thread {} start", Thread.currentThread().getName());
        Connection connection = connectionHolder.get();

        try {
            if (connection == null || connection.isClosed()) {
                try {
                    connection = dataSource.getConnection();
                } catch (SQLException e) {
                    log.error("SQL error with closing connection", e);
                    throw new RuntimeException(e);
                }
                connectionHolder.set(connection);
            }
        } catch (SQLException e) {
            log.error("SQL error with closing connection", e);
            throw new RuntimeException(e);
        }
        log.info("Connection getting successful");
        return connection;
    }

    public void closeConnection() {
        log.info("Closing connection from thread {} start", Thread.currentThread().getName());
        Connection connection = connectionHolder.get();

        try {
            if (connection == null || connection.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            log.error("SQL error with closing connection", e);
            throw new RuntimeException(e);
        }

        try {
            connection.close();
        } catch (SQLException e) {
            log.error("SQL error with closing connection");
            throw new RuntimeException(e);
        } finally {
            connectionHolder.remove();
        }
        log.info("Closing connection from thread {} finish", Thread.currentThread().getName());
    }
}
