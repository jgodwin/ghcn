package com.ghcn;

import java.util.Arrays;
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
	DataSourceDAO dao;
	
	void setStationDAO(DataSourceDAO dao){
		this.dao = dao;
	}
	
	@CrossOrigin
	@RequestMapping(value="/inventory", method=RequestMethod.GET)
	public Set<Inventory> fetchInventories(@RequestParam(name="stations",required=true) 
		Station.Delegate[] delegates){
		return dao.getInventory(new HashSet<>(Arrays.asList(delegates)));
	}
	
    @CrossOrigin
	@RequestMapping(value="/stations",method=RequestMethod.GET)
	public Stations fetchStations(
//			@RequestParam(name="states",defaultValue="",required=false) String rawStates){
//		if ("".equals(rawStates)) return new Stations(dao.getStations());
//		else {
//			Set<String> states = new HashSet<>();
//			String[] split = rawStates.split(",");
//			for(String state: split) states.add(state);
//			return new Stations(dao.getStations(states));
//		}
			@RequestParam(name="states",required=false) String[] states){
    	System.out.println("FETCHING: ");	
    	for(String s: states){
    		System.out.println("STATE: "+s);
    	}
    	
    	if (states == null || states.length == 0){
    		return new Stations(dao.getStations());
    	} else {
    		return new Stations(dao.getStations(
    				new HashSet<>(Arrays.asList(states))));
    	}
	}
//    Does not work...?
//    @RequestMapping({"/","/home"})
//    public String showHome(){
//    	return "/resources/index.jsp";
//    }
}
