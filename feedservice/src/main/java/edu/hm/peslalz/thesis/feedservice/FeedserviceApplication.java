package edu.hm.peslalz.thesis.feedservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeedserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedserviceApplication.class, args);
	}

}
