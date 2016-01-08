package com.ghcn;

import java.util.Set;

/**
 * Contains more contextual information about a particular station.
 * This is separated into another class because there are many instances
 * where we will not need the inventory information, and don't want
 * to waste time populating those fields for each individual station.
 */
public class Inventory {
	private Station.Delegate station;
	private Set<Element> elements;
	
	/**
	 * @param delegate a representation of the station that this inventory belongs to
	 * @param elements the set of elements that belong to this inventory, held by 
	 * 	reference.
	 */
	public Inventory(Station.Delegate delegate, Set<Element> elements){
		this.station = delegate;
		this.elements = elements;
	}
	public Station.Delegate getDelegate() {
		return station;
	}
	public Set<Element> getElements() {
		return elements;
	}
	
	/**
	 * A span of time, given as a range of years, inclusive.
	 */
	public static class YearRange {
		private int startYear;
		private int endYear;
		public YearRange(int start, int end){
			startYear = start;
			endYear = end;
		}
		public int getEndYear() {
			return endYear;
		}
		public int getStartYear() {
			return startYear;
		}
	}
}
