package by.niruin.ticketRegistrationApp.listener;

import by.niruin.ticketRegistrationApp.constant.AttributeName;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static by.niruin.ticketRegistrationApp.constant.AttributeName.DATA_SOURCE;


@WebListener
public class InitAttributeServletContextListener implements ServletContextListener {
    private static final String DATASOURCE_PROPERTIES_PATH = "/WEB-INF/classes/datasource.properties";
    private static final Logger log = LogManager.getLogger(InitAttributeServletContextListener.class);
    private final Properties properties = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Start attribute initialization");
        loadProperties();
        var context = sce.getServletContext();

        context.setAttribute(DATA_SOURCE, initHikariDataSource());
        log.info("Attribute initialization finished");
    }

    private void loadProperties() {
        try (var inputStream = new FileInputStream(DATASOURCE_PROPERTIES_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Property load error", e);
            throw new RuntimeException(e);
        }
    }

    private DataSource initHikariDataSource() {
        var config = new HikariConfig(properties);

        return new HikariDataSource(config);
    }
}
