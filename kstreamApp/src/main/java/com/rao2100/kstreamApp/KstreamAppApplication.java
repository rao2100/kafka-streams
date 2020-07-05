package com.rao2100.kstreamApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.rao2100"})
public class KstreamAppApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(KstreamAppApplication.class);

	// @Autowired
	// AppConfig appConfig;

	@Autowired
	Runnable runnable; 

	// @Autowired
	// WordCountStream wordCountStream;

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(KstreamAppApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		
		LOG.info("EXECUTING : command line runner");
		// wordCountStream.runWordCount();
		// wordCountStream.countWords();

		runnable.run();
		
		
	}

}
