package com.ghcn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Holds the actual time series data for a particular station for 
 * a particular physical observed quantity.
 */
public class Observation {
	
	private Station.Delegate station;
	private Element.Delegate element;
	private Unit unit;
	private int[] months;
	private int[] years;
	private Object[][] data;
	
	public Observation(Station.Delegate station, Element.Delegate element, 
			int[] years, int[] months, float[][] data, float nullValue){
		this.station = station;
		this.element = element;
		int nd = years.length;
		int count = 0;
		for(float[] d: data){
			count += d.length;
		}
		
		this.data = new Object[count][2];
		int index = 0;
		for(int i = 0; i < nd; ++i){
			float[] mdata = data[i];
			for(int j =0; j < mdata.length; j++, index++){
				Calendar c = new GregorianCalendar();
				c.set(years[i], months[i]-1, j, 0, 0, 0);
				this.data[index][0] = c.getTimeInMillis();
				this.data[index][1] = mdata[j] <= nullValue ? null : mdata[j];
			}
		}
		this.unit = unit;
		this.months = months;
		this.years = years;
	}
	
	public int[] getMonth() {
		return months;
	}
	public int[] getYear() {
		return years;
	}
	public Object[][] getData() {
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
