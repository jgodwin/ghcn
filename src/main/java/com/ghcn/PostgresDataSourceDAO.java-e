package com.ghcn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ghcn.Station.Delegate;

/**
 * A {@link DataSourceDAO} that connects to a PostGres database
 * and pulls representative information out of the underlying SQL
 * tables to generate POJOs to be used inside of the web app.
 */
@Repository
public class PostgresDataSourceDAO implements DataSourceDAO{
	
	/*
	 * DB col names
	 */
	private static final String INVENTORY_LY = "lastyear";
	private static final String INVENTORY_FY = "firstyear";
	private static final String INVENTORY_ELEMENT = "element";
	
	private static final String STATION_ID = "sid";
	private static final String STATION_LONGITUDE = "long";
	private static final String STATION_LATITUDE = "lat";
	private static final String STATION_NAME = "name";
	private static final String STATION_STATE="state";
	
	private static final String STATION_TABLE="Station";
	private static final String INVENTORY_TABLE="Inventory";
	private static final String OBS_TABLE="Observation";
	private static final String STATE_TABLE="State";
	private static final String COUNTRY_TABLE="Country";

	private static String sub(String query, Object... cols){
		return String.format(query, cols);
	}
	
	private static String buildInQuery(String col, Set<?> inObjects, boolean wantSemiColon){
		String qry = sub("WHERE %s IN (",col);
		int n = inObjects.size();
		for(int i =0; i < n; ++i){
			if (i >0) qry+=",";
			qry+="?";
		}
		qry+=")";
		return wantSemiColon ? qry+";": qry;
	}
	
	private JdbcTemplate jdbc;
	
	@Autowired
	public PostgresDataSourceDAO(JdbcTemplate template){
		this.jdbc = template;
	}
	
	@Override
	public Set<Observation> getObservations(
			Set<Station.Delegate> stations, 
			Set<Element.Delegate> elements) {
		String qry = sub("SELECT sid, ")
	}
	
@Override
	public Set<Inventory> getInventory(Set<Station.Delegate> stations) {
		String qry = 
				sub("SELECT %s,%s,%s,%s from %s ",
						STATION_ID,INVENTORY_ELEMENT, INVENTORY_FY, INVENTORY_LY, INVENTORY_TABLE) +
				buildInQuery(STATION_ID, stations, true);
		Set<String> sids = new HashSet<>();
		stations.forEach((delegate) -> sids.add(delegate.getId()));
		List<Element> query = jdbc.query(qry, new InventoryElementMapper(), sids.toArray());
		Map<String,Set<Element>> stationMap = new HashMap<>();
		query.forEach((element)-> {
			String sid = element.getStation().getId();
			Set<Element> elems = stationMap.containsKey(sid) ? 
					stationMap.get(sid) : new HashSet<>();
			elems.add(element);
			stationMap.put(sid, elems);
		});
		Set<Inventory> inventories = new HashSet<>();
		stationMap.forEach((sid, elements) -> {
			inventories.add(new Inventory(new Station.Delegate(sid),elements));
		});
		return inventories;
	}

	@Override
	public List<Station> getStations() {
		return jdbc.query(
				sub("SELECT * FROM %s;",STATION_TABLE), new StationMapper());
	}
	@Override
	public List<Station> getStations(Set<String> states) {
		//Ugly because Postgres does not support mapping Sets or Lists to single ? fields,
		// as apparently MySql and others do =(
		String query = sub(
				"SELECT %s, %s, %s, %s FROM %s ", 
					STATION_ID, STATION_NAME, STATION_LATITUDE, STATION_LONGITUDE, STATION_TABLE);
		query+=buildInQuery(STATION_STATE, states, true);
		return jdbc.query(query,
				new StationMapper(),states.toArray() );
	}
	
	
	private static class InventoryElementMapper implements RowMapper<Element> {
		@Override
		public Element mapRow(ResultSet rs, int arg1) throws SQLException {
			return new Element(
					new Station.Delegate(rs.getString(STATION_ID)), 
					rs.getString(INVENTORY_ELEMENT), 
						new Inventory.YearRange(
							rs.getInt(INVENTORY_FY), rs.getInt(INVENTORY_LY)));
		}
	}

	private static class StationMapper implements RowMapper<Station>{
		@Override
		public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Station(rs.getString(STATION_ID), rs.getString(STATION_NAME), 
					rs.getFloat(STATION_LATITUDE), rs.getFloat(STATION_LONGITUDE));
		}
	}

}
