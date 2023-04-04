package itmo.lab.sdb;

import itmo.lab.sdb.events.DatabaseDataImportedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Value("classpath://mySQLScript.sql")
    private Resource mySQLScript;

    @Value("classpath://postgresSQLScript.sql")
    private Resource postgresSQLScript;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Bean
    @Primary
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder  = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .setType(EmbeddedDatabaseType.H2)
                .setName(EmbeddedDatabaseType.H2.name())
                .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                .build();
    }

    @Bean(name = "mySQLDataSource")
    @ConfigurationProperties(prefix = "spring.mysql.datasource")
    public DataSource mySQLDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.postgres.datasource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void importData() {
        ResourceDatabasePopulator resourcePostgresPopulator = new ResourceDatabasePopulator(postgresSQLScript);
        ResourceDatabasePopulator resourceMySQLPopulator = new ResourceDatabasePopulator(mySQLScript);

        resourcePostgresPopulator.execute(postgresDataSource());
        resourceMySQLPopulator.execute(mySQLDataSource());

        applicationEventPublisher.publishEvent(new DatabaseDataImportedEvent(this));
    }

}
