package itmo.lab.sdb;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

	public static void main(String[] args) {
		SpringApplication.run(SdbApplication.class, args);
	}

	@Scheduled(cron = "0 */1 * * * ?")
	public void perform() throws Exception {
		jobLauncher.run(readBusinessNewsFromFileJob, new JobParameters());
		jobLauncher.run(importIndexDataJob, new JobParameters());
		jobLauncher.run(saveDataFromMySQLToPostgresJob, new JobParameters());
	}

}
