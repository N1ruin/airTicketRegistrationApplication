package repository.impl;

import domain.Passport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.SessionHelper;
import repository.PassportRepository;

import java.util.List;
import java.util.Optional;

public class PassportRepositoryImpl implements PassportRepository {
    public static final Logger log = LogManager.getLogger(PassportRepositoryImpl.class);
    private final SessionHelper sessionHelper;

    public PassportRepositoryImpl(SessionHelper sessionHelper) {
        this.sessionHelper = sessionHelper;
    }

    @Override
    public Passport save(Passport passport) {
        var session = sessionHelper.getSession();
        session.persist(passport);

        return passport;
    }

    @Override
    public Optional<Passport> findById(Long id) {
        var session = sessionHelper.getSession();
        var passport = session.find(Passport.class, id);

        return Optional.ofNullable(passport);
    }

    @Override
    public List<Passport> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Passport update(Passport passport) {
        var session = sessionHelper.getSession();

        return session.merge(passport);
    }

    @Override
    public void deleteById(Long id) {
        var session = sessionHelper.getSession();
        var passport = session.find(Passport.class, id);

        if (passport != null) {
            session.remove(passport);
        }
    }

    @Override
    public Optional<Passport> findBySeriesAndNumberAndCitizenship(String series, String number, String citizenship) {
        var hql = """
                FROM Passport passport
                WHERE passport.series = ?1
                AND passport.number = ?2
                AND passport.citizenship = ?3
                """;
        var session = sessionHelper.getSession();
        return session.createQuery(hql, Passport.class)
                .setParameter(1, series)
                .setParameter(2, number)
                .setParameter(3, citizenship)
                .uniqueResultOptional();
    }
}
