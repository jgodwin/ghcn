package com.ghcn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresStationProvider implements StationDAO{
	
	private JdbcTemplate jdbc;
	
	@Autowired
	public PostgresStationProvider(JdbcTemplate template){
		this.jdbc = template;
	}

	@Override
	public List<Station> getStations() {
		return jdbc.query("SELECT * FROM STATION;", new StationMapper());
	}
	@Override
	public List<Station> getStations(Set<String> states) {
		//Ugly because Postgres does not support mapping Sets or Lists to single ? fields,
		// as apparently MySql and others do =(
		String query = "SELECT sid, name, lat, long FROM Station WHERE State IN (";
		int n = states.size();
		for(int i =0; i < n; ++i){
			if (i >0) query+=",";
			query+="?";
		}
		query+=");";
		return jdbc.query(query,
				new StationMapper(),states.toArray() );
	}
}
