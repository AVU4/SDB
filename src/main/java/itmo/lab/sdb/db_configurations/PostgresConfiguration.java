package itmo.lab.sdb.db_configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class PostgresConfiguration {

    @Value("classpath://postgresSQLScript.sql")
    private Resource postgresSQLScript;

    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.postgres.datasource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void performDatabaseScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(postgresSQLScript);
        resourceDatabasePopulator.execute(postgresDataSource());
    }

}
