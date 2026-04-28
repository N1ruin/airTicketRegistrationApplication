package repository.impl;

import domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.SessionHelper;
import repository.UserRepository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public UserRepositoryImpl(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        var session = sessionHelper.getSession();

        var hql = "FROM User user WHERE user.email = ?1";

        return session.createQuery(hql, User.class)
                .setParameter(1, email)
                .uniqueResultOptional();
    }

    @Override
    public User save(User user) {
        var session = sessionHelper.getSession();

        session.persist(user);

        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        var session = sessionHelper.getSession();

        var user = session.find(User.class, id);

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        var hql = "FROM User";

        var session = sessionHelper.getSession();

        return session.createQuery(hql, User.class).getResultList();
    }

    @Override
    public User update(User user) {
        var session = sessionHelper.getSession();

        session.merge(user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        var session = sessionHelper.getSession();
        var user = session.find(User.class, id);

        if (user != null) {
            session.remove(user);
        }
    }
}
