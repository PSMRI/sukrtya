package com.piramal.sukrtya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.piramal.sukrtya.repository", repositoryFactoryBeanClass = org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean.class)

public class SpringBootSukrtyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSukrtyaApplication.class, args);
	}

}
