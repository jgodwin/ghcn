package com.ghcn;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StationMapper implements RowMapper<Station>{

	@Override
	public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Station(rs.getString("sid"), rs.getString("name"), 
				rs.getFloat("lat"), rs.getFloat("long"));
//		return new Station(rs.getString("sid"));
	}
}
