package itmo.lab.sdb.postgres;

import itmo.lab.sdb.mongo.IndexData;
import itmo.lab.sdb.mysql.BusinessNews;
import itmo.lab.sdb.mysql.BusinessNewsMapper;
import itmo.lab.sdb.processors.MOEXIndexFromBusinessNewsProcessor;
import itmo.lab.sdb.processors.MOEXIndexFromIndexDataProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

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
                .sql("INSERT INTO moex (DATE, TITLE, SUMMARY, INDEX_VALUE) VALUES (?, ?, ?, ?)")
                .itemPreparedStatementSetter(new MOEXIndexPreparedStatementSetter())
                .build();
    }

    @Bean(name = "updateMOEXIndexToPostgres")
    public JdbcBatchItemWriter<MOEXIndexResult> updateMOEXIndexToPostgres(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<MOEXIndexResult>()
                .dataSource(postgresDataSource)
                .sql("UPDATE moex SET INDEX_VALUE=? WHERE DATE=?")
                .assertUpdates(false)
                .itemPreparedStatementSetter(new MOEXIndexUpdateStatementSetter())
                .build();
    }

    @Bean
    public Step saveDataFromMySQLToPostgresStep(JobRepository jobRepository,
                                                PlatformTransactionManager transactionManager,
                                                ItemReader<BusinessNews> mySQLReader,
                                                MOEXIndexFromBusinessNewsProcessor moexIndexFromBusinessNewsProcessor,
                                                JdbcBatchItemWriter<MOEXIndexResult> writeMOEXIndexToPostgres) {
        return new StepBuilder("saveDataFromMySQLToPostgresStep", jobRepository)
                .<BusinessNews, MOEXIndexResult>chunk(10)
                .transactionManager(transactionManager)
                .reader(mySQLReader)
                .processor(moexIndexFromBusinessNewsProcessor)
                .writer(writeMOEXIndexToPostgres)
                .build();
    }

    @Bean
    public MongoItemReader<IndexData> mongoIndexDataReader(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<IndexData>()
                .name("mongoIndexDataReader")
                .collection("index")
                .template(mongoTemplate)
                .jsonQuery("db.index.findAll()")
                .sorts(Collections.emptyMap())
                .targetType(IndexData.class)
                .build();
    }

    @Bean
    public Step saveDataFromMongoToPostgresStep(JobRepository jobRepository,
                                                PlatformTransactionManager transactionManager,
                                                MongoItemReader<IndexData> mongoIndexDataReader,
                                                @Qualifier("updateMOEXIndexToPostgres") JdbcBatchItemWriter<MOEXIndexResult> updateMOEXIndexToPostgres,
                                                MOEXIndexFromIndexDataProcessor indexDataProcessor) {
        return new StepBuilder("saveDataFromMongoToPostgresStep", jobRepository)
                .<IndexData, MOEXIndexResult>chunk(10)
                .transactionManager(transactionManager)
                .reader(mongoIndexDataReader)
                .processor(indexDataProcessor)
                .writer(updateMOEXIndexToPostgres)
                .build();

    }

    @Bean
    public Job saveDataFromMongoToPostgresJob(JobRepository jobRepository,
                                              Step saveDataFromMongoToPostgresStep) {
        return new JobBuilder("saveDataFromMongoToPostgresJob", jobRepository)
                .start(saveDataFromMongoToPostgresStep)
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void performDatabaseScript() {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(postgresSQLScript);
        resourceDatabasePopulator.execute(postgresDataSource());
    }

}
