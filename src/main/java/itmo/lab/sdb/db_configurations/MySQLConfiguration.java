package itmo.lab.sdb.db_configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
public class MySQLConfiguration {

    @Value("classpath://mySQLScript.sql")
    private Resource mySQLScript;


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

    @EventListener(ApplicationReadyEvent.class)
    public void performDatabaseScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(mySQLScript);
        resourceDatabasePopulator.execute(mySQLDataSource());
    }
}
