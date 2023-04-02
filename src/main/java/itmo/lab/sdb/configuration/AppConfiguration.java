package itmo.lab.sdb.configuration;

import itmo.lab.sdb.entities.BusinessNews;
import itmo.lab.sdb.processors.ConsoleOutputProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class AppConfiguration {

    @Value("classpath:/data.tsv")
    private Resource tsvFile;

    @Bean
    public Job readBusinessNewsFromFileJob(JobRepository jobRepository, Step importBusinessNewsStep) {
        return new JobBuilder("readBusinessNewsFromFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importBusinessNewsStep)
                .build();
    }

    @Bean
    public Step importBusinessNewsStep(JobRepository jobRepository, ConsoleOutputProcessor consoleOutputProcessor, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("importBusinessNewsStep", jobRepository)
                .<BusinessNews, BusinessNews>chunk(10)
                .transactionManager(platformTransactionManager)
                .reader(businessNewsReader())
                .processor(consoleOutputProcessor)
                .writer((item) -> {})
                .build();
    }

    @Bean
    public FlatFileItemReader<BusinessNews> businessNewsReader() {
        return new FlatFileItemReaderBuilder<BusinessNews>()
                .name("BusinessNewsFromFileReader")
                .linesToSkip(1)
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .resource(tsvFile)
                .delimited()
                .delimiter(DelimitedLineTokenizer.DELIMITER_TAB)
                .names("title", "score", "link", "summary", "published", "tickers")
                .targetType(BusinessNews.class)
                .build();
    }

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder  = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                .build();
    }


}
