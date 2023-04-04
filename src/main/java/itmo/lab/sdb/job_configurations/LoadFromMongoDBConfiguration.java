package itmo.lab.sdb.job_configurations;

import itmo.lab.sdb.entities.IndexData;
import itmo.lab.sdb.entities.MOEXIndexResult;
import itmo.lab.sdb.setters.MOEXIndexUpdateStatementSetter;
import itmo.lab.sdb.processors.MOEXIndexFromIndexDataProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class LoadFromMongoDBConfiguration {

    @Bean
    public Job saveDataFromMongoToPostgresJob(JobRepository jobRepository,
                                              Step saveDataFromMongoToPostgresStep) {
        return new JobBuilder("saveDataFromMongoToPostgresJob", jobRepository)
                .start(saveDataFromMongoToPostgresStep)
                .build();
    }

    @Bean
    public Step saveDataFromMongoToPostgresStep(JobRepository jobRepository,
                                                PlatformTransactionManager transactionManager,
                                                MongoItemReader<IndexData> mongoIndexDataReader,
                                                JdbcBatchItemWriter<MOEXIndexResult> updateMOEXIndexToPostgres) {
        return new StepBuilder("saveDataFromMongoToPostgresStep", jobRepository)
                .<IndexData, MOEXIndexResult>chunk(10)
                .transactionManager(transactionManager)
                .reader(mongoIndexDataReader)
                .processor(new MOEXIndexFromIndexDataProcessor())
                .writer(updateMOEXIndexToPostgres)
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
    public JdbcBatchItemWriter<MOEXIndexResult> updateMOEXIndexToPostgres(@Qualifier("postgresDataSource") DataSource postgresDataSource) {
        return new JdbcBatchItemWriterBuilder<MOEXIndexResult>()
                .dataSource(postgresDataSource)
                .sql("UPDATE moex SET INDEX_VALUE=? WHERE DATE=?")
                .assertUpdates(false)
                .itemPreparedStatementSetter(new MOEXIndexUpdateStatementSetter())
                .build();
    }

}
