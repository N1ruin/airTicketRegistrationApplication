package service;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class MigrationService {
    private final DataSource dataSource;

    public MigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() {
        try {
            Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
        } catch (Throwable t) {
            System.out.println("DEBUG: CRITICAL ERROR DURING INIT: " + t.getMessage());
            t.printStackTrace(System.out); // Печатаем в OUT для Testcontainers
        }
    }
}
