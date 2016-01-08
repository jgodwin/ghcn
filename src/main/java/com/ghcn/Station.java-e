package com.ghcn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines an observation station, which has a unique ID,
 * user readable name, and a geographic location on the Earth.
 */
public class Station {
	
	private String id;
	private String name;
	private float latitude, longitude; //In degrees
	
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
	
	/**
	 * A representation of a station that can be used to gather more information
	 * about the station.  This allows client code to send only station IDs back
	 * to the server to make further queries.
	 */
	public static class Delegate {
		private String id;
		public Delegate(String id){
			this.id = id;
		}
		/**
		 * DO NOT CALL, only used by Spring to instantiate through reflection with setter
		 */
		public Delegate(){
			
		}
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		@Override
		public int hashCode() {
			return id.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			else if (obj.getClass().getName().equals(getClass().getName())){
				return ((Delegate)obj).id.equals(id);
			} else {
				return false;
			}
		}
	}
}
