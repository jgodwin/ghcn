package com.ghcn;

import com.ghcn.Inventory.YearRange;
import com.ghcn.Station.Delegate;

/**
 * An element represents a single set of related physical observations for
 * a particular station, that span a time range.
 */
public class Element {
	private String name;
	private YearRange range;
	private Station.Delegate station;
	public Element(Station.Delegate station, String name, YearRange range){
		this.station = station;
		this.name = name;
		this.range = range;
	}
	public String getName() {
		return name;
	}
	public Station.Delegate getStation() {
		return station;
	}
	public YearRange getRange() {
		return range;
	}
	
	public static class Delegate {
		private String name;
		public Delegate(String name){
			this.name = name;
		}
		public Delegate(){
			
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		
	}
}