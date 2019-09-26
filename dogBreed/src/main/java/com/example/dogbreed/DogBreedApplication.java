package com.example.dogbreed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DogBreedApplication {

	public static void main(String[] args) {
		final ConfigurableApplicationContext run = SpringApplication.run(DogBreedApplication.class, args);
	}

}
