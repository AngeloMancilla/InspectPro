package com._9.inspect_pro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com._9.inspect_pro.repository")
public class InspectProApplication {

	public static void main(String[] args) {
		SpringApplication.run(InspectProApplication.class, args);
	}

}
