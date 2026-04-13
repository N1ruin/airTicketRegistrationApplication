package repository;

import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionHelper {
    private final ConnectionHelper connectionHelper;

    public TransactionHelper(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        var isFirstTransaction = false;
        try (var connection = connectionHelper.getConnection()) {
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
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionHelper.closeConnection();
        }
    }

    public void executeInTransaction(Runnable action) {
        executeInTransaction(() -> {
            action.run();
            return null;
        });
    }
}
