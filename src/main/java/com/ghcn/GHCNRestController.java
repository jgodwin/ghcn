package com.ghcn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GHCNRestController {
	
	@Autowired
	StationDAO dao;
	
	void setStationDAO(StationDAO dao){
		this.dao = dao;
	}
	
    @CrossOrigin
	@RequestMapping(value="/stations",method=RequestMethod.GET)
	public Stations fetchStations(
			@RequestParam(name="states",defaultValue="",required=false) String rawStates){
		if ("".equals(rawStates)) return new Stations(dao.getStations());
		else {
			Set<String> states = new HashSet<>();
			String[] split = rawStates.split(",");
			for(String state: split) states.add(state);
			return new Stations(dao.getStations(states));
		}
	}
}
