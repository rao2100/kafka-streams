package com.rao2100.kstreamApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KstreamAppApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(KstreamAppApplication.class);

	@Autowired
	AppConfig appConfig;

	@Autowired
	WordCountStream wordCountStream;

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(KstreamAppApplication.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("EXECUTING : command line runner");
  
        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
		}

		System.out.println(appConfig.getUrl());
		System.out.println(appConfig.getUsername());
		System.out.println(appConfig.getPassword());

		wordCountStream.runWordCount();
		

	}

}
