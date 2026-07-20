package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionHelper {
    private static final Logger log = LogManager.getLogger(TransactionHelper.class);

    private final ConnectionHolder connectionHolder;

    public TransactionHelper(ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        var connection = connectionHolder.getConnection();
        var isOuterTransaction = false;
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                isOuterTransaction = true;
            }

            T result = action.get();

            if (isOuterTransaction) {
                connection.commit();
            }

            return result;
        } catch (Exception e) {
            try {
                if (!connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }

            connectionHolder.clearConnection();

            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        } finally {
            if (isOuterTransaction) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    log.error("Failed to reset auto-commit", e);
                }
            }
        }
    }

    public void executeInTransaction(Runnable action) {
        executeInTransaction(() -> {
            action.run();
            return null;
        });
    }
}
