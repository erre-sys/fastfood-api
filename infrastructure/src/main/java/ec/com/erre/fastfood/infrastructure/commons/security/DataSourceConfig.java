package ec.com.erre.fastfood.infrastructure.commons.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.hikari.maximum-pool-size:10}")
	private Integer maxPoolSize;

	@Value("${spring.datasource.hikari.minimum-idle:2}")
	private Integer minimumIdle;

	@Value("${spring.datasource.hikari.idle-timeout:30000}")
	private Integer idleTimeout;

	@Bean
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setMaximumPoolSize(maxPoolSize);
		ds.setMinimumIdle(minimumIdle);
		ds.setIdleTimeout(idleTimeout);
		return ds;
	}
}
