package repository;

import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionHelper {
    private final ConnectionHelper connectionHelper;

    public TransactionHelper(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        var connection = connectionHelper.getConnection();
        var isFirstTransaction = false;
        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                isFirstTransaction = true;
            }

            T result = action.get();

            if (isFirstTransaction) {
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

            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        } finally {
            if (isFirstTransaction) {
                connectionHelper.closeConnection();
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
