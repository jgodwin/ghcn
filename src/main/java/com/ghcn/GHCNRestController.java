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

@CrossOrigin
@RestController
public class GHCNRestController {
	
	@Autowired
	DataSourceDAO dao;
	
	void setStationDAO(DataSourceDAO dao){
		this.dao = dao;
	}
	
	private static <T> Set<T> toSet(T[] array){
		if (array == null) return new HashSet<T>();
		return new HashSet<T>(Arrays.asList(array));
	}
	
	@RequestMapping(value="/elements", method=RequestMethod.GET)
	public List<Element.Delegate> getElements(){
		return dao.getElements();
	}
	
	@RequestMapping(value="/states")
	public List<String> getStates(){
		return dao.getStates();
	}

	@RequestMapping(value="/observation", method=RequestMethod.GET)
	public List<Observation> fetchObservations(
			@RequestParam(name="stations",required=true) Station.Delegate[] stations,
			@RequestParam(name="elements",required=true) Element.Delegate[] elements,
			@RequestParam(name="years", required=false) YearRange years){
		return dao.getObservations(
				toSet(stations), toSet(elements), years);
	}
	
	@RequestMapping(value="/inventory", method=RequestMethod.GET)
	public List<Inventory> fetchInventories(@RequestParam(name="stations",required=true) 
		Station.Delegate[] delegates){
		return dao.getInventory(toSet(delegates));
	}
	
	@RequestMapping(value="/years", method=RequestMethod.GET)
	public YearRange getYearRange(){
		return dao.getFirstLastYearRange();
	}
	
	@RequestMapping(value="/stations",method=RequestMethod.GET)
	public Stations fetchStations(
			@RequestParam(name="countries", required=false) String[] countries,
			@RequestParam(name="states",required=false) String[] states,
			@RequestParam(name="elements",required=false) Element.Delegate[] elements,
			@RequestParam(name="bbox", required=false) BoundingBox box,
			@RequestParam(name="years", required=false) YearRange years){
    		return new Stations(
    				dao.getStations(
    				toSet(states), toSet(countries),toSet(elements),box, years));
	}
}
