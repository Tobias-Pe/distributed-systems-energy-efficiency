package edu.hm.peslalz.thesis.postservice.client;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
	@Bean
	public Retryer retryer() {
		return new Retryer.Default(300, 2000, 5);
	}
}
