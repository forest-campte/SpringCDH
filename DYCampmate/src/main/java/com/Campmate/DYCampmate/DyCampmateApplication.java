package com.Campmate.DYCampmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DyCampmateApplication {

	public static void main(String[] args) {

		SpringApplication.run(DyCampmateApplication.class, args);
	}

}
