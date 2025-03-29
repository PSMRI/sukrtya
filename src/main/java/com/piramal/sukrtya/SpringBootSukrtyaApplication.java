package com.piramal.sukrtya;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@OpenAPIDefinition(servers = {
		@Server(url = "https://sukrtya.api.shsbihar.in", description = "104 - Production Server"),
		@Server(url = "https://api.sukrtya.in", description = "Production Server"),
		@Server(url = "https://dev-ui.sukrtya.in", description = "Tekdi - Production Server"),
		@Server(url = "http://localhost:8080", description = "Development Server")
})

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.piramal.sukrtya.repository", repositoryFactoryBeanClass = org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean.class)

public class SpringBootSukrtyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSukrtyaApplication.class, args);
	}

}
