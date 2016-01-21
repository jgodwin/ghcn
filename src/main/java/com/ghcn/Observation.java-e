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
	private int month;
	private int year;
	
//	public Observation(Station.Delegate station, Element.Delegate element, 
//			int year, int month, float[] data, Unit unit){
	public Observation(Station.Delegate station, Element.Delegate element, 
			int year, int month, float[] data){
		this.station = station;
		this.element = element;
		this.data = data;
		this.unit = unit;
		this.month = month;
		this.year = year;
	}
	
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
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
