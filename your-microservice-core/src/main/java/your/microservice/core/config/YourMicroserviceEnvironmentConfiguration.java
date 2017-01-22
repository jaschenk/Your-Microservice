package your.microservice.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * YourMicroserviceEnvironmentConfiguration
 *
 * @author jeff.a.schenk@gmail.com on 2/27/16.
 */
@ComponentScan(basePackages = {"your.microservice.core",
        "your.microservice.idp"})
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("your.microservice.idp.model.base")
public class YourMicroserviceEnvironmentConfiguration {
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(YourMicroserviceEnvironmentConfiguration.class);

    /**
     * Enables Property Loading.
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Configuration Environment
     */
    @Autowired
    private Environment environment;

    /**
     * Initialization of this Configuration.
     */
    @PostConstruct
    public void initialization() {
        LOGGER.info("Your Microservice Environment Configuration Initialization...");

    }

    /**
     * H2 Server Backend
     * @return org.h2.tools.Server
     * @throws SQLException
     */
    @Bean(initMethod="start",destroyMethod="stop")
    public org.h2.tools.Server h2WebConsoleServer () throws SQLException {
        return org.h2.tools.Server.createWebServer("-web","-webAllowOthers","-webDaemon","-webPort", "8082");
    }

    /**
     * DataSource
     * @return DataSource
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.idp")
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("your.microservice.idp.model.base");
        factory.setDataSource(dataSource());

        return factory;
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public ResourceDatabasePopulator initDatabase(DataSource dataSource) throws Exception {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript((new ClassPathResource("db/h2/insert-data.sql")));
        populator.populate(dataSource.getConnection());
        return populator;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

}
