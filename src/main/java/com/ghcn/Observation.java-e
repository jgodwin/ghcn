package com.ghcn;

/**
 * Holds the actual time series data for a particular station for 
 * a particular physical observed quantity.
 */
public class Observation {
	
	private float[] data;
	private Station.Delegate station;
	private Element.Delegate element;
	private Unit unit;
	
	public Observation(Station.Delegate station, Element.Delegate element, float[] data, Unit unit){
		this.station = station;
		this.element = element;
		this.data = data;
		this.unit = unit;
	}
	
	public float[] getData() {
		return data;
	}
	public Element.Delegate getElement() {
		return element;
	}
	public Station.Delegate getStation() {
		return station;
	}
	public Unit getUnit() {
		return unit;
	}
	public static class Unit {
		private String name;
		public Unit(String name){
			this.name = name;
		}
		public  String getName() {
			return name;
		}
	}
	
}
