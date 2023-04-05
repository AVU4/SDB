package itmo.lab.sdb.job_configurations;

import itmo.lab.sdb.entities.MOEXIndexResult;
import itmo.lab.sdb.mappers.MOEXIndexResultMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class SaveDataToCSVFileConfiguration {

    @Autowired
    @Qualifier("postgresDataSource")
    private DataSource postgresDataSource;

    private final WritableResource resultData = new FileSystemResource("/home/result/result_data.tsv");

    @Bean
    public ItemReader<MOEXIndexResult> readData() {
        return new JdbcCursorItemReaderBuilder<MOEXIndexResult>()
                .name("readData")
                .dataSource(postgresDataSource)
                .rowMapper(new MOEXIndexResultMapper())
                .sql("select * from moex")
                .build();
    }

    @Bean
    public FlatFileItemWriter<MOEXIndexResult> saveDataToCSV() {
        return new FlatFileItemWriterBuilder<MOEXIndexResult>()
                .name("saveData")
                .resource(resultData)
                .append(false)
                .delimited()
                .delimiter("\t")
                .names("dayId" , "title" ,"summary", "indexValue")
                .shouldDeleteIfExists(true)
                .build();

    }

    @Bean
    public Job saveDataToCSVJob(JobRepository jobRepository, Step saveDataToCSVStep) {
        return new JobBuilder("saveDatatToCSV", jobRepository)
                .start(saveDataToCSVStep)
                .build();
    }

    @Bean
    public Step saveDataToCSVStep(JobRepository jobRepository, ItemReader<MOEXIndexResult> readData,
                                  FlatFileItemWriter<MOEXIndexResult> saveDataToCSV,
                                  PlatformTransactionManager transactionManager) {
        return new StepBuilder("saveDataToCSV", jobRepository)
                .<MOEXIndexResult, MOEXIndexResult>chunk(10)
                .transactionManager(transactionManager)
                .reader(readData)
                .writer(saveDataToCSV)
                .build();
    }
}
