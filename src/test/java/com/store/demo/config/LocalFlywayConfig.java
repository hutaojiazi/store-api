package com.store.demo.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring configurations for Flyway.
 */
@Profile("dev")
@Configuration
public class LocalFlywayConfig
{
	/**
	 * This configuration cleans up the database and the execute the flyway migration for dev environment.
	 * In order to have same behavior of dropping and creating the db schema for 'dev' profile
	 */
	@Bean
	public FlywayMigrationStrategy devFlywayMigrationStrategy()
	{
		return flyway ->
		{
			flyway.clean();
			flyway.migrate();
		};
	}
}
