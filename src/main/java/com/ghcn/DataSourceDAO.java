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
			Set<Station.Delegate> stations, Element.Delegate elements,  YearRange years);
	
	/**
	 * Get the inventories for each station.
	 * @param stations
	 * @return
	 */
	public List<Inventory> getInventory(Set<Station.Delegate> stations);
	
	/**
	 * Get the range of data available in the dataset. The startYear, is simply the min
	 * of the Inventory firstyear column, the endYear is the max of the Inventory lastYear column, which
	 * should be the current year if the dataset is up-to-date.
	 * @return
	 */
	public YearRange getFirstLastYearRange();
	
	/**
	 * Get the list of all elements in the dataset.
	 * @return
	 */
	public List<Element.Delegate> getElements();
	
	public List<String> getStates();
	
	/**
	 * Get the stations that pass the given filters.  The filters are AND'd together, in pseudocode:
	 * SELECT * From STATION where 
	 * STATION.State IN STATES AND 
	 * STATION.Country IN COUNTRIES AND 
	 * STATION.Location IN BOUNDING BOX AND
	 * STATION HAS DATA IN RANGE(YEARS)
	 * @param states the stations must be in one of the given states (US).  Pass emptySet if no filtering on states.
	 * @param countries the stations must be in one of the given countries (supersedes the states filter), ie if
	 * countries != USA and states is specified, then countries are used first. Pass emptySet if no filtering on countries.
	 * @param box the geographic bounding box that stations must fall in.
	 * @param range the year range, the station must have started recording <= the startYear and endedRecording >= endYear. null if no filtering on year range.
	 * @return the list of stations that pass, if no filters are specified, then this is equivalent to {@link #getStations()}
	 */
    public List<Station> getStations(Set<String> states, Set<String> countries, Set<Element.Delegate> elements, BoundingBox box, YearRange range);
	
	/**
	 * @return Return the list of all stations in the dataset.
	 */
	public List<Station> getStations();
	
}
