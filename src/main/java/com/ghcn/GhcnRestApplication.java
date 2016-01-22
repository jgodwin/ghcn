package com.ghcn;

import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class GhcnRestApplication {
	
	@Bean
	DataSource getDataSource(){
		PGPoolingDataSource dataSource = new PGPoolingDataSource();
		dataSource.setServerName("localhost");
		dataSource.setPortNumber(5432);
		dataSource.setUser(System.getenv("POSTGRES_USER"));
		dataSource.setPassword(System.getenv("POSTGRES_PW"));
		dataSource.setDatabaseName("ghcn");
		return dataSource;
	}
	
	@Bean
	JdbcTemplate getJdbcTemplate(DataSource ds){
		return new JdbcTemplate(ds);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(GhcnRestApplication.class, args);
	}
}
