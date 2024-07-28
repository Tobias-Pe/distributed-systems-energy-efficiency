package edu.hm.peslalz.thesis.feedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class FeedserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedserviceApplication.class, args);
	}

}
