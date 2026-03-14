package listener;

import constant.AttributeName;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.MigrationService;

@WebListener
public class MigrationContextListener implements ServletContextListener {
    private static final Logger log = LogManager.getLogger(MigrationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Database migration started");

        var context = sce.getServletContext();

        var migrationService = (MigrationService) context.getAttribute(AttributeName.MIGRATION_SERVICE);

        migrationService.migrate();

        log.info("Database migration finish");
    }
}
