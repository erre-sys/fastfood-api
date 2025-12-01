package ec.com.erre.fastfood.infrastructure.commons.security;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;
	@Value("${spring.datasource.username}")
	private String dataSourceUsername;
	@Value("${spring.datasource.password}")
	private String dataSourcePassword;
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private Integer dataSourcePoolSize;
	@Value("${spring.datasource.hikari.minimum-idle}")
	private Integer minimumIdle;
	@Value("${spring.datasource.hikari.idle-timeout}")
	private Integer idleTimeout;

	@Bean
	DataSource dataSource() {
		AbstractRoutingDataSource routingDataSource = new UserRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();
		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(createHikariDataSource(dataSourceUsername));
		return routingDataSource;
	}

	DataSource createHikariDataSource(String username) {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(dataSourceUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(dataSourcePassword);
		dataSource.setMaximumPoolSize(dataSourcePoolSize == null ? 1 : dataSourcePoolSize);
		dataSource.setMinimumIdle(minimumIdle);
		dataSource.setIdleTimeout(idleTimeout);

		return dataSource;
	}
}
