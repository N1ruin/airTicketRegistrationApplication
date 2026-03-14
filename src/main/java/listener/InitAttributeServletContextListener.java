package listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sequrity.PasswordEncoder;
import service.MigrationService;


import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static constant.AttributeName.*;

@WebListener
public class InitAttributeServletContextListener implements ServletContextListener {
    private static final String DATASOURCE_PROPERTIES_PATH = "datasource.properties";
    private static final Logger log = LogManager.getLogger(InitAttributeServletContextListener.class);
    private final Properties properties = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Start attribute initialization");

        var context = sce.getServletContext();

        loadProperties();

        var dataSource = initHikariDataSource();
        context.setAttribute(DATA_SOURCE, dataSource);

        var migrationService = new MigrationService(dataSource);
        context.setAttribute(MIGRATION_SERVICE, migrationService);

        var passwordEncoder = new PasswordEncoder();
        context.setAttribute(PASSWORD_ENCODER, passwordEncoder);

        log.info("Attribute initialization finish");
    }

    private void loadProperties() {
        log.info("Start properties loading");

        try (var inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(DATASOURCE_PROPERTIES_PATH)) {

            if (inputStream == null) {
                throw new IOException("Properties file not found with path %s".formatted(DATA_SOURCE));
            }

            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Property load error", e);
            throw new RuntimeException(e);
        }

        log.info("Properties loading finish");
    }

    private DataSource initHikariDataSource() {
        log.info("Start loading hikariCP configuration");
        var config = new HikariConfig(properties);
        log.info("HikariCP configuration loading finish");

        return new HikariDataSource(config);
    }
}
