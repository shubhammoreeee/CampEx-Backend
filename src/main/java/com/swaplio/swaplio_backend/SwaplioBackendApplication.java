package com.swaplio.swaplio_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SwaplioBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwaplioBackendApplication.class, args);
	}

}
