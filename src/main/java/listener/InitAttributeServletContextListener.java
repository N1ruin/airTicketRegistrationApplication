package listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import constant.AttributeName;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import mapper.user.UserAuthenticationResponseMapper;
import mapper.user.UserResultSetMapper;
import mapper.user.UserSignUpRequestMapper;
import mapper.user.UserSignUpResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.ls.LSOutput;
import repository.ConnectionHelper;
import repository.TransactionHelper;
import repository.impl.UserRepositoryImpl;
import sequrity.PasswordEncoder;
import service.HttpHelper;
import service.MigrationService;
import service.UserService;
import validation.UserValidationService;


import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static constant.AttributeName.*;

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

        var objectMapper = new ObjectMapper();
        context.setAttribute(OBJECT_MAPPER, objectMapper);

        var httpHelper = new HttpHelper(objectMapper);
        context.setAttribute(HTTP_HELPER, httpHelper);

        var connectionHelper = new ConnectionHelper(dataSource);
        context.setAttribute(CONNECTION_HELPER, connectionHelper);

        var userResultSetMapper = new UserResultSetMapper();
        context.setAttribute(USER_RESULT_SET_MAPPER, userResultSetMapper);

        var userRepository = new UserRepositoryImpl(connectionHelper, userResultSetMapper);
        context.setAttribute(USER_REPOSITORY, userRepository);

        var userSignUpRequestMapper = new UserSignUpRequestMapper(passwordEncoder);
        context.setAttribute(USER_SIGN_UP_REQUEST_MAPPER, userSignUpRequestMapper);

        var userSignUpResponseMapper = new UserSignUpResponseMapper();
        context.setAttribute(USER_SIGN_UP_RESPONSE_MAPPER, userSignUpResponseMapper);

        var transactionHelper = new TransactionHelper(connectionHelper);
        context.setAttribute(TRANSACTION_HELPER, transactionHelper);

        var userValidationService = new UserValidationService();
        context.setAttribute(USER_VALIDATION_SERVICE, userValidationService);

        var userAuthenticationResponseMapper = new UserAuthenticationResponseMapper();
        context.setAttribute(USER_AUTH_RESPONSE_MAPPER, userAuthenticationResponseMapper);

        var userService = new UserService(userRepository, transactionHelper, userSignUpRequestMapper,
                userSignUpResponseMapper, userValidationService, passwordEncoder, userAuthenticationResponseMapper);
        context.setAttribute(USER_SERVICE, userService);

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
