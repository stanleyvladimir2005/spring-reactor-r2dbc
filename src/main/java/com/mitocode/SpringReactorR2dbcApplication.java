package com.mitocode;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@SpringBootApplication
public class SpringReactorR2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringReactorR2dbcApplication.class, args);
	}

	@Bean
	public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		var initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		var populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
		initializer.setDatabasePopulator(populator);
		return initializer;
	}
}