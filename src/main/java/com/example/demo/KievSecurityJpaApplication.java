package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.demo.model.BusinessObject;
import com.example.demo.model.User;
import com.example.demo.repo.BusinessObjectRepository;

@SpringBootApplication
public class KievSecurityJpaApplication {
	private static final Logger log = LoggerFactory.getLogger(KievSecurityJpaApplication.class);

//	@Bean
//	public CommandLineRunner demo(BusinessObjectRepository repository) {
//		return (args) -> {
//			// save a few business objects
//
//			User owner = new User("kostik", "mostik", "abc@gmail.com");
//			String data = "whatever";
//			BusinessObject bo = new BusinessObject(data, owner);
//			repository.save(bo);
//			log.info("--------");
//
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(KievSecurityJpaApplication.class, args);
	}

	@Configuration
	@EnableJpaRepositories
	static class RepositoryConfiguration {
	}

}
