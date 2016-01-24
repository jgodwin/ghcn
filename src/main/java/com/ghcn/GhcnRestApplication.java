package com.ghcn;

import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@SpringBootApplication
public class GhcnRestApplication extends WebMvcConfigurerAdapter{
	
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
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		super.addFormatters(registry);
		registry.addConverter(new YearRange.Parser());
		registry.addConverter(new BoundingBox.Parser());
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new PrintInterceptor());
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(GhcnRestApplication.class, args);
	}
}
