package itmo.lab.sdb.job_configurations;

import itmo.lab.sdb.entities.IndexData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportDataToMongoDBConfiguration {

    @Value("classpath://index_dataset.csv")
    private Resource indexDataset;

    @Bean
    public FlatFileItemReader<IndexData> indexDataReader() {
        return new FlatFileItemReaderBuilder<IndexData>()
                .name("IndexDataReader")
                .linesToSkip(1)
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .resource(indexDataset)
                .targetType(IndexData.class)
                .delimited()
                .delimiter(";")
                .delimiter(DelimitedLineTokenizer.DELIMITER_TAB)
                .includedFields(2, 3, 4, 5, 6, 7)
                .names("TRADEDATE", "OPEN", "HIGH", "LOW", "CLOSE", "VALUE")
                .build();
    }

    @Bean
    public MongoItemWriter<IndexData> indexDataWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<IndexData>()
                .collection("index")
                .template(mongoTemplate)
                .build();
    }

    @Bean
    public Job importIndexDataJob(JobRepository jobRepository,
                                  Step importIndexDataStep) {
        return new JobBuilder("importIndexDataJob", jobRepository)
                .start(importIndexDataStep)
                .build();
    }

    @Bean
    public Step importIndexDataStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    FlatFileItemReader<IndexData> indexDataReader,
                                    MongoItemWriter<IndexData> indexDataWriter) {
        return new StepBuilder("importIndexData", jobRepository)
                .<IndexData, IndexData>chunk(10)
                .transactionManager(transactionManager)
                .reader(indexDataReader)
                .writer(indexDataWriter)
                .build();
    }
}
