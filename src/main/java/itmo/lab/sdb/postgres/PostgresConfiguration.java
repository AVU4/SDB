package itmo.lab.sdb.postgres;

import itmo.lab.sdb.mysql.BusinessNews;
import itmo.lab.sdb.mysql.BusinessNewsMapper;
import itmo.lab.sdb.processors.MOEXIndexProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PostgresConfiguration {

    @Value("classpath://postgresSQLScript.sql")
    private Resource postgresSQLScript;

    @Bean(name = "postgresDataSource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://postgres_db:5432/result")
                .username("root")
                .password("password")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "saveDataFromMySQLToPostgresJob")
    public Job saveDataFromMySQLToPostgresJob(JobRepository jobRepository,
                                              Step saveDataFromMySQLToPostgresStep) {
        return new JobBuilder("saveDataFromMySQLToPostgres", jobRepository)
                .start(saveDataFromMySQLToPostgresStep)
                .build();
    }

    @Bean(name="mySQLReader")
    public ItemReader<BusinessNews> mySQLReader(@Qualifier("mySQLDataSource") DataSource mySQLDataSource) {
        return new JdbcCursorItemReaderBuilder<BusinessNews>()
                .name("MySQLReader")
                .dataSource(mySQLDataSource)
                .sql("SELECT PUBLISHED, SUMMARY, TITLE FROM BUSINESS_NEWS")
                .rowMapper(new BusinessNewsMapper())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<MOEXIndexResult> writeMOEXIndexToPostgres(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<MOEXIndexResult>()
                .dataSource(postgresDataSource)
                .sql("INSERT INTO moex (DATE, TITLE, SUMMARY, INDEX_VALUES) VALUES (?, ?, ?, ?)")
                .itemPreparedStatementSetter(new MOEXIndexPreparedStatementSetter())
                .build();
    }

    @Bean
    public Step saveDataFromMySQLToPostgresStep(JobRepository jobRepository,
                                                PlatformTransactionManager transactionManager,
                                                ItemReader<BusinessNews> mySQLReader,
                                                MOEXIndexProcessor moexIndexProcessor,
                                                JdbcBatchItemWriter<MOEXIndexResult> writeMOEXIndexToPostgres) {
        return new StepBuilder("saveDataFromMySQLToPostgresStep", jobRepository)
                .<BusinessNews, MOEXIndexResult>chunk(10)
                .transactionManager(transactionManager)
                .reader(mySQLReader)
                .processor(moexIndexProcessor)
                .writer(writeMOEXIndexToPostgres)
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void performDatabaseScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(postgresSQLScript);
        resourceDatabasePopulator.execute(postgresDataSource());
    }

}
