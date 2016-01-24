package com.ghcn;

import org.springframework.core.convert.converter.Converter;

public class BoundingBox {
	
	private float _minLat;
	private float _maxLat;
	private float _minLong;
	private float _maxLong;

	public BoundingBox(float minLat, float maxLat, float minLong, float maxLong){
		_minLat = minLat;
		_maxLat = maxLat;
		_minLong = minLong;
		_maxLong = maxLong;
	}
	public float getMinLat(){
		return _minLat;
	}
	
	public float getMaxLat(){
		return _maxLat;
	}
	
	public float getMinLong(){
		return _minLong;
	}
	
	public float getMaxLong(){
		return _maxLong;
	}
	
	// For reflection
	public BoundingBox(){
	}
	public void setMinLat(float min){
		_minLat = min;
	}
	public void setMaxLat(float max){
		_maxLat = max;
	}
	public void setMinLong(float min){
		_minLong = min;
	}
	public void setMaxLong(float max){
		_maxLong = max;
	}
	
	public static class Parser implements Converter<String,BoundingBox> {

		@Override
		public BoundingBox convert(String s) {
			if (s == null || s.length() == 0) return null;
			String[] coords = s.split(",");
			if (coords.length != 4) return null;
			try{
				float minLat = Float.parseFloat(coords[0]);
				float minLong = Float.parseFloat(coords[1]);
				float maxLat  = Float.parseFloat(coords[2]);
				float maxLong = Float.parseFloat(coords[3]);
				return new BoundingBox(minLat,maxLat,minLong,maxLong);
			} catch(NumberFormatException nfe){
				return null;
			}
		}
		
	}

}
