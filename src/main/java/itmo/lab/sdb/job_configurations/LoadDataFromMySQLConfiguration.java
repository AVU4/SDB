package itmo.lab.sdb.job_configurations;

import itmo.lab.sdb.entities.BusinessNews;
import itmo.lab.sdb.mappers.BusinessNewsMapper;
import itmo.lab.sdb.setters.MOEXIndexPreparedStatementSetter;
import itmo.lab.sdb.entities.MOEXIndexResult;
import itmo.lab.sdb.processors.MOEXIndexFromBusinessNewsProcessor;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class LoadDataFromMySQLConfiguration {

    @Bean
    public Job saveDataFromMySQLToPostgresJob(JobRepository jobRepository,
                                              Step saveDataFromMySQLToPostgresStep) {
        return new JobBuilder("saveDataFromMySQLToPostgres", jobRepository)
                .start(saveDataFromMySQLToPostgresStep)
                .build();
    }

    @Bean
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
}
