package com.ghcn;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * The class that will actually interface with whatever data store, and return back
 * POJOs for the rest of the app.  Designed to be easily swapped to try different
 * backend implementations.  All the details of how fields are mapped to underlying
 * data store structures are handled within this class.
 */
@Component
public interface DataSourceDAO {
	
	/**
	 * Return an observation for each station for each element if it exists
	 * for that particular station.  The total # of observations returned
	 * should be: stations.size()*elements.size()
	 * @param stations
	 * @param elements
	 * @return
	 */
	public List<Observation> getObservations(
			Set<Station.Delegate> stations, Set<Element.Delegate> elements);
	
	/**
	 * Get the inventories for each station.
	 * @param stations
	 * @return
	 */
	public List<Inventory> getInventory(Set<Station.Delegate> stations);

	/**
	 * Return the stations for each state.
	 * @param states
	 * @return
	 */
	public List<Station> getStations(Set<String> states);
	
	/**
	 * @return Return the list of all stations in the dataset.
	 */
	public List<Station> getStations();
	
}
