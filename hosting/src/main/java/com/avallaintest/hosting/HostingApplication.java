package com.avallaintest.hosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.avallaintest.hosting.dao")
@EntityScan("com.avallaintest.hosting.model")
public class HostingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostingApplication.class, args);
	}

}
