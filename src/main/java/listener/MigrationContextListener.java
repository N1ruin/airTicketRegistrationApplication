package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

import static constant.ServletContextAttributeKey.DATA_SOURCE;

public class MigrationContextListener implements ServletContextListener {
    private static final Logger log = LogManager.getLogger(MigrationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Database migration started");

        var dataSource = (DataSource) sce.getServletContext().getAttribute(DATA_SOURCE);
        applyMigrations(dataSource);

        log.info("Database migration finished");
    }

    private void applyMigrations(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}
