package com.ghcn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Station {
	
	private String id;
	private String name;
	private float latitude, longitude;
	
	public Station(String id,String name, float latitude, float longitude){
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public float getLatitude() {
		return latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	
	public String toString(){
		return id+":"+name;
	}
}
