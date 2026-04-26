package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SessionHelper {
    private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<>();
    private final SessionFactory sessionFactory;

    public SessionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        var session = sessionHolder.get();
        if (session == null) {
            session = sessionFactory.openSession();
            sessionHolder.set(session);
        }
        return session;
    }

    public void closeSession() {
        Session session = sessionHolder.get();

        if (session != null) {
            session.close();
        }

        sessionHolder.remove();
    }
}
