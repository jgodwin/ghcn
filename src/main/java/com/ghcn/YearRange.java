package com.ghcn;

import org.springframework.core.convert.converter.Converter;

/**
 * A span of time, given as a range of years, inclusive.
 */
public class YearRange {
	private int startYear;
	private int endYear;
	public YearRange(int start, int end){
		startYear = start;
		endYear = end;
	}
	public YearRange(){
		//For reflection
	}
	public void setStartYear(int start){
		startYear = start;
	}
	public void setEndYear(int end){
		endYear = end;
	}
	public int getEndYear() {
		return endYear;
	}
	public int getStartYear() {
		return startYear;
	}
	
	public static class Parser implements Converter<String, YearRange> {
		@Override
		public YearRange convert(String yr) {
			if (yr == null || yr.length() == 0) return null;
			String[] startEnd = yr.split(",");
			if (startEnd == null || startEnd.length != 2) return null;
			try {
				int start = Integer.parseInt(startEnd[0]);
				int end   = Integer.parseInt(startEnd[1]);
				return new YearRange(start,end);
			} catch (NumberFormatException nfe){
				return null;
			}
		}
	}
}