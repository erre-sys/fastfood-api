package ec.com.erre.fastfood.infrastructure.commons.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class UserRoutingDataSource extends AbstractRoutingDataSource {

	@Autowired
	private DataSourceConfig dataSourceConfig;

	private final Map<String, DataSource> userDataSourceMap = new HashMap<>();

	private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

	@Override
	protected Object determineCurrentLookupKey() {
		return ConnectedUser.userName.get();
	}

	@Override
	public void afterPropertiesSet() {
		setTargetDataSources(targetDataSources);
		super.afterPropertiesSet();
	}

	public void addTargetDataSource(String key, DataSource dataSource) {
		targetDataSources.put(key, dataSource);
		setTargetDataSources(targetDataSources);
		afterPropertiesSet();
	}

	@Override
	public Connection getConnection() throws SQLException {
		String username = ConnectedUser.userName.get();

		// Check if DataSource already exists for the user, if not create it
		if (username != null && !userDataSourceMap.containsKey(username)) {
			DataSource dataSource = dataSourceConfig.createHikariDataSource(username);
			userDataSourceMap.put(username, dataSource);
			this.addTargetDataSource(username, dataSource);
		}
		return super.getConnection();
	}

}
