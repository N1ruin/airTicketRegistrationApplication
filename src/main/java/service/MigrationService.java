package service;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class MigrationService {
    private final DataSource dataSource;

    public MigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
