package listener;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static constant.AttributeName.DATA_SOURCE;

public class DestroyServletContextListener implements ServletContextListener {
    private static final Logger log = LogManager.getLogger(DestroyServletContextListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application destroy start");

        var context = sce.getServletContext();

        log.info("Hikari data source close start");

        var dataSource = (HikariDataSource) context.getAttribute(DATA_SOURCE);
        dataSource.close();

        log.info("Hikari data source close finish");

        log.info("Application destroy finish");
    }
}
