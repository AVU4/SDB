package itmo.lab.sdb.mysql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
@Configuration
public class MySQLConfiguration {

    @Value("classpath://mySQLScript.sql")
    private Resource mySQLScript;

    @Bean(name = "mySQLDataSource")
    public DataSource mySQLDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://db_1:3306/news")
                .username("root")
                .password("password")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void performDatabaseScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(mySQLScript);
        resourceDatabasePopulator.execute(mySQLDataSource());
    }
}
