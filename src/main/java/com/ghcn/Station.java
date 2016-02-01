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
	public static class Delegate implements Comparable<Delegate>{
		private String id;
		public Delegate(String id){
			this.id = id;
		}
		
		public Delegate(){
			
		}
		public String getId() {
			return id;
		}
		
		@Override
		public int compareTo(Delegate o) {
			return id.compareTo(o.id);
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
			if (obj == null || obj.getClass() != getClass()) return false;
			else {
				return ((Delegate)obj).id.equals(id);
			} 
		}
	}
}
