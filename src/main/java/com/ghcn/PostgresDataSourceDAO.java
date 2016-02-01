package com.ghcn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	private static final String STATION_LOCATION="location";
	
	private static final String OBSERVATION_VALUE_PREFIX="value";
	private static final String OBSERVATION_MONTH="month";
	private static final String OBSERVATION_YEAR="year";
	private static final String OBSERVATION_ELEMENT="element";
	private static final int OBSERVATION_NUM_COLS=31;
	private static final float OBSERVATION_NULL_VALUE=-9999.0f;
	
	private static final String STATION_TABLE="Station";
	private static final String INVENTORY_TABLE="Inventory";
	private static final String OBS_TABLE="Observation";
	private static final String STATE_TABLE="State";
	private static final String COUNTRY_TABLE="Country";
	private static final String UNIT_TABLE="Unit";
	
	private static final String UNIT_ELEMENT = "element";
	

	private static String sub(String query, Object... cols){
		return String.format(query, cols);
	}
	
	private <T> List<T> query(String query, RowMapper<T> mapper, Object[] objs){
		System.out.println("EXECUTING: "+query);
		return jdbc.query(query, mapper, objs);
	}
	
	private <T> List<T> query(String query, RowMapper<T> mapper){
		return query(query, mapper, null);
	}
	
	private static String buildInQuery(String col, Set<?> inObjects, boolean wantSemiColon){
		String qry = sub(" %s IN (",col);
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
	
    public List<Station> getStations(YearRange range){
    	String qry = sub("SELECT * from %s where %s IN ", STATE_TABLE, STATION_ID)+
    			sub("(SELECT DISTINCT %s from %s where %s <=? AND %s >=?", 
    					STATION_ID, INVENTORY_TABLE, INVENTORY_FY, INVENTORY_LY);
    	return query(qry, new StationMapper(), 
    			new Object[]{range.getStartYear(),range.getEndYear()});
    }
    
    @Override
    public YearRange getFirstLastYearRange() {
    	String qry = sub("SELECT min(%s), max(%s) from %s;",INVENTORY_FY,INVENTORY_LY,INVENTORY_TABLE);
    	List<YearRange> results = query(qry, new RowMapper<YearRange>(){
    		@Override
    		public YearRange mapRow(ResultSet rs, int arg1) throws SQLException {
    			// These column names are hardcoded from Postgres
    			return new YearRange(rs.getInt("min"), rs.getInt("max"));
    		}
    	});
    	if (results.size() > 0) return results.get(0);
    	return new YearRange(2000,2016); // Fall back to default
    }
	
	@Override
	public List<Observation> getObservations(
			Set<Station.Delegate> stations, 
			Element.Delegate element, YearRange years) {
		Set<Element.Delegate> elements = Collections.singleton(element);
		String qry = sub("SELECT * FROM %s WHERE ", OBS_TABLE);
		qry+=buildInQuery(STATION_ID, stations, false);
		qry+=" AND "+buildInQuery(INVENTORY_ELEMENT, elements, true);
		List<String> objs = new ArrayList<>();
		stations.forEach((station) -> objs.add(station.getId()));
		elements.forEach((e)-> objs.add(e.getName()));
		List<SingleMonthObservation> months = query(qry, new SingleMonthObservationElementMapper(), objs.toArray());
		Map<Station.Delegate, Set<SingleMonthObservation>> obsMap = new HashMap<>();
		for(SingleMonthObservation obs: months){
			Set<SingleMonthObservation> observations = obsMap.containsKey(obs.delegate) ? 
					obsMap.get(obs.delegate) : new HashSet<>();
			observations.add(obs);
			obsMap.put(obs.delegate, observations);
		}
		List<Observation> observations = new ArrayList<>();
		for(Entry<Station.Delegate, Set<SingleMonthObservation>> pair: obsMap.entrySet()){
			Delegate delegate = pair.getKey();
			Set<SingleMonthObservation> monthObservations = pair.getValue();
			int n = monthObservations.size();
			int[] monthArray = new int[n];
			int[] yearArray  = new int[n];
			float[][] vals = new float[n][];
			int i = 0; 
			List<SingleMonthObservation> sortedObservations = new ArrayList<>(monthObservations);
			sortedObservations.sort(new Comparator<SingleMonthObservation>(){
				@Override
				public int compare(SingleMonthObservation o1, SingleMonthObservation o2) {
					int result = Integer.compare(o1.year, o2.year);
					if (result == 0) result = Integer.compare(o1.month, o2.month);
					return result;
				}
			});
			for(SingleMonthObservation mo: sortedObservations){
				monthArray[i] = mo.month;
				yearArray[i] = mo.year;
				vals[i] = mo.values;
				i++;
			}
			observations.add(new Observation(delegate, element, yearArray, monthArray, vals, OBSERVATION_NULL_VALUE));
		}
		return observations;
	}
	
	@Override
	public List<com.ghcn.Element.Delegate> getElements() {
		List<com.ghcn.Element.Delegate> delegates = 
			query(sub("Select distinct %s from %s;", INVENTORY_ELEMENT, INVENTORY_TABLE),
				new RowMapper<Element.Delegate>(){
			@Override
			public com.ghcn.Element.Delegate mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Element.Delegate(rs.getString(INVENTORY_ELEMENT));
			}
		});
		//We want these sorted in most cases, or we could introduce arbitrary sorting / aliasing here
		delegates.sort(new Comparator<Element.Delegate>(){
			@Override
			public int compare(com.ghcn.Element.Delegate o1, com.ghcn.Element.Delegate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return delegates;
	}
	
@Override
	public List<Inventory> getInventory(Set<Station.Delegate> stations) {
		String qry = 
				sub("SELECT %s,%s,%s,%s from %s WHERE",
						STATION_ID,INVENTORY_ELEMENT, INVENTORY_FY, INVENTORY_LY, INVENTORY_TABLE) +
				buildInQuery(STATION_ID, stations, true);
		Set<String> sids = new HashSet<>();
		stations.forEach((delegate) -> sids.add(delegate.getId()));
		List<Element> query = query(qry, new InventoryElementMapper(), sids.toArray());
		Map<String,Set<Element>> stationMap = new HashMap<>();
		query.forEach((element)-> {
			String sid = element.getStation().getId();
			Set<Element> elems = stationMap.containsKey(sid) ? 
					stationMap.get(sid) : new HashSet<>();
			elems.add(element);
			stationMap.put(sid, elems);
		});
		List<Inventory> inventories = new ArrayList<>();
		stationMap.forEach((sid, elements) -> {
			inventories.add(new Inventory(new Station.Delegate(sid),elements));
		});
		return inventories;
	}

	@Override
	public List<String> getStates() {
		String qry = sub("Select DISTINCT %s from %s",STATION_STATE,STATION_TABLE);
		return query(qry, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(STATION_STATE);
			}
		});
	}

	@Override
	public List<Station> getStations() {
		return query(
				sub("SELECT * FROM %s;",
						STATION_TABLE), new StationMapper());
	}
	@Override
	public List<Station> getStations(Set<String> states, Set<String> countries, Set<Element.Delegate> elements, 
			BoundingBox box, YearRange years) {
		if (states.size() == 0 && countries.size() == 0 && elements.size() == 0 && 
				years == null && box == null) return getStations();
		
		//Ugly because Postgres does not support mapping Sets or Lists to single ? fields,
		// as apparently MySql and others do =(
		String query = sub(
				"SELECT %s, %s, %s, %s FROM %s WHERE ", 
					STATION_ID, STATION_NAME, STATION_LATITUDE, STATION_LONGITUDE, STATION_TABLE);
		List<Object> params = new ArrayList<>();
		boolean other = false;
		if (states.size() > 0){
			query+=buildInQuery(STATION_STATE, states, false);
			params.addAll(states);
			other = true;
		}
		if (countries.size() > 0){
			//TODO add country column, and add country filtering
		}
		if (elements.size() > 0){
			if (other) query+=" AND ";
			query+=sub("%s in (SELECT DISTINCT %s from %s where ",
					STATION_ID, STATION_ID, INVENTORY_TABLE)+buildInQuery(INVENTORY_ELEMENT, elements, false);
			params.addAll(elements);
			other = true;
		}
		if (box != null){
			if (other) query+=" AND ";
			// This should work now that we're storing as geometry instead of geography
			query+=sub(
					"ST_Intersects(ST_MakeEnvelope(?,?,?,?,4326),%s)",
					STATION_LOCATION);
			params.add(box.getMinLong());
			params.add(box.getMinLat());
			params.add(box.getMaxLong());
			params.add(box.getMaxLat());
//			query+=sub(
//			" %s BETWEEN ? AND ? AND %s BETWEEN ? AND ?",
//			STATION_LATITUDE, STATION_LONGITUDE);
			// Params needed to truncate great circle results
//			params.add(box.getMinLat());
//			params.add(box.getMaxLat());
//			params.add(box.getMinLong());
//			params.add(box.getMaxLong());
			other = true;
		}
		if (years != null){
			if (other) query+=" AND ";
			query+=sub("%s IN (SELECT DISTINCT %s from %s where %s <=? AND %s >=?)", 
					STATION_ID,STATION_ID, INVENTORY_TABLE, INVENTORY_FY, INVENTORY_LY);
			params.add(years.getStartYear());
			params.add(years.getEndYear());
		}
		query+=";";
		return query(query,
				new StationMapper(),params.toArray());
	}
	
	private static class SingleMonthObservation {
		public final int month, year;
		public final float[] values;
		public final Station.Delegate delegate;
		public final Element.Delegate element;
		public SingleMonthObservation(Station.Delegate delegate, Element.Delegate element, int year, int month, float[] values){
			this.delegate = delegate;
			this.element  = element;
			this.month = month;
			this.year  = year;
			this.values = values;
		}
	}
	
	private static class SingleMonthObservationElementMapper implements RowMapper<SingleMonthObservation> {
		@Override
		public SingleMonthObservation mapRow(ResultSet rs, int arg1) throws SQLException {
			float[] values = new float[OBSERVATION_NUM_COLS];
			for(int i =1 ; i <= OBSERVATION_NUM_COLS; ++i){
				String raw = rs.getString(OBSERVATION_VALUE_PREFIX+i);
				float val = -9999;
				if (raw != null){
					val = Float.parseFloat(raw);
				}
				values[i-1] = val;
			}
			int month = rs.getInt(OBSERVATION_MONTH);
			int year = rs.getInt(OBSERVATION_YEAR);
			String element = rs.getString(OBSERVATION_ELEMENT);
			String sid = rs.getString(STATION_ID);
			
			return new SingleMonthObservation(
					new Station.Delegate(sid),
					new Element.Delegate(element),
					year, month,
					values);
		}
	}
	
	
	private static class InventoryElementMapper implements RowMapper<Element> {
		@Override
		public Element mapRow(ResultSet rs, int arg1) throws SQLException {
			return new Element(
					new Station.Delegate(rs.getString(STATION_ID)), 
					rs.getString(INVENTORY_ELEMENT), 
						new YearRange(
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
