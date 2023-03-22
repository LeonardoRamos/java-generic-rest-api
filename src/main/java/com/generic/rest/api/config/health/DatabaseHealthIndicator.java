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

import com.generic.rest.api.BaseConstants.HEALTHCHECK;

public class DatabaseHealthIndicator extends AbstractHealthIndicator implements InitializingBean {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public DatabaseHealthIndicator() {
		this(null);
	}

	public DatabaseHealthIndicator(DataSource dataSource) {
		super(HEALTHCHECK.DATABASE.FAILED_MESSAGE);
		this.dataSource = dataSource;
		jdbcTemplate = (dataSource != null) ? new JdbcTemplate(dataSource) : null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataSource == null) {
			throw new IllegalStateException(HEALTHCHECK.DATABASE.NO_DATASOURCE);
		}
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (dataSource == null) {
			builder.up().withDetail(HEALTHCHECK.DATABASE.KEY, HEALTHCHECK.DATABASE.UNKNOWN_DATASOURCE);
		
		} else {
			doDataSourceHealthCheck(builder);
		}
	}

	private void doDataSourceHealthCheck(Health.Builder builder) {
		builder.up().withDetail(HEALTHCHECK.DATABASE.KEY, getProduct());
		builder.status(Boolean.TRUE.equals(isConnectionValid()) ? Status.UP : Status.DOWN);
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