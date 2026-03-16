package repository;

import java.sql.SQLException;
import java.util.function.Supplier;

public class TransactionHelper {
    private final ConnectionHelper connectionHelper;

    public TransactionHelper(ConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        try (var connection = connectionHelper.getConnection()) {
            try {
                connection.setAutoCommit(false);

                T result = action.get();

                connection.commit();

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
}
