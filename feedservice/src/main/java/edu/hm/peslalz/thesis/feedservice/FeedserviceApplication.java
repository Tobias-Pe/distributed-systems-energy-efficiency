package edu.hm.peslalz.thesis.feedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FeedserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedserviceApplication.class, args);
	}

}
