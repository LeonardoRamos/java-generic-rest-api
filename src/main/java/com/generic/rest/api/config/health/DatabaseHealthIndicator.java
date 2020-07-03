package com.generic.rest.api.config.health;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.generic.rest.api.Constants.HEALTH_CHECK;

public class DatabaseHealthIndicator extends AbstractHealthIndicator implements InitializingBean {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public DatabaseHealthIndicator() {
		this(null);
	}

	public DatabaseHealthIndicator(DataSource dataSource) {
		super(HEALTH_CHECK.DATABASE.FAILED_MESSAGE);
		this.dataSource = dataSource;
		jdbcTemplate = (dataSource != null) ? new JdbcTemplate(dataSource) : null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataSource == null) {
			throw new IllegalStateException(HEALTH_CHECK.DATABASE.NO_DATASOURCE);
		}
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (dataSource == null) {
			builder.up().withDetail(HEALTH_CHECK.DATABASE.KEY, HEALTH_CHECK.DATABASE.UNKNOWN_DATASOURCE);
		
		} else {
			doDataSourceHealthCheck(builder);
		}
	}

	private void doDataSourceHealthCheck(Health.Builder builder) throws Exception {
		builder.up().withDetail(HEALTH_CHECK.DATABASE.KEY, getProduct());
		builder.status(isConnectionValid() ? Status.UP : Status.DOWN);
	}

	private String getProduct() {
		return jdbcTemplate.execute((ConnectionCallback<String>) this::getProduct);
	}

	private String getProduct(Connection connection) throws SQLException {
		return connection.getMetaData().getDatabaseProductName();
	}

	private Boolean isConnectionValid() {
		return jdbcTemplate.execute((ConnectionCallback<Boolean>) this::isConnectionValid);
	}

	private Boolean isConnectionValid(Connection connection) throws SQLException {
		return connection.isValid(0);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

}