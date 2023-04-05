package itmo.lab.sdb;

import itmo.lab.sdb.events.DatabaseDataImportedEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
public class SdbApplication {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job readBusinessNewsFromFileJob;

	@Autowired
	private Job saveDataFromMySQLToPostgresJob;

	@Autowired
	private Job importIndexDataJob;

	@Autowired
	private Job saveDataFromMongoToPostgresJob;

	@Autowired
	private Job saveDataToCSVJob;


	public static void main(String[] args) {
		SpringApplication.run(SdbApplication.class, args);
	}

	@EventListener(DatabaseDataImportedEvent.class)
	public void performImportData() throws Exception {
		jobLauncher.run(importIndexDataJob, new JobParameters());
		jobLauncher.run(readBusinessNewsFromFileJob, new JobParameters());
	}

	@Scheduled(cron = "0 3/3 * * * ?")
	public void perform() throws Exception {
		jobLauncher.run(saveDataFromMySQLToPostgresJob, new JobParameters());
		jobLauncher.run(saveDataFromMongoToPostgresJob, new JobParameters());
		jobLauncher.run(saveDataToCSVJob, new JobParameters());
	}
}
