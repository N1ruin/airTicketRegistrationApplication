package repository;

import org.hibernate.Transaction;

import java.util.function.Supplier;

public class TransactionHelper {
    private final SessionHelper sessionHelper;

    public TransactionHelper(SessionHelper connectionHelper) {
        this.sessionHelper = connectionHelper;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        var session = sessionHelper.getSession();
        Transaction transaction = null;
        var isFirstTransaction = false;

        try {
            if (!session.getTransaction().isActive()) {
                transaction = session.beginTransaction();
                isFirstTransaction = true;
            } else {
                transaction = session.getTransaction();
            }

            T result = action.get();

            if (isFirstTransaction && transaction != null) {
                transaction.commit();
            }

            return result;
        } catch (Exception e) {
            if (isFirstTransaction && transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception exception) {
                    e.addSuppressed(exception);
                }
            }

            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        } finally {
            if (isFirstTransaction) {
                sessionHelper.closeSession();
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
