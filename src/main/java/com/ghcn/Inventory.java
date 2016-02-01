package com.ghcn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Contains more contextual information about a particular station.
 * This is separated into another class because there are many instances
 * where we will not need the inventory information, and don't want
 * to waste time populating those fields for each individual station.
 */
public class Inventory {
	private Station.Delegate station;
	private List<Element> elements;
	
	/**
	 * @param delegate a representation of the station that this inventory belongs to
	 * @param elements the set of elements that belong to this inventory, held by 
	 * 	reference.
	 */
	public Inventory(Station.Delegate delegate, Set<Element> elements){
		this.station = delegate;
		this.elements = new ArrayList<>(elements);
		// Want these sorted, so that they make sense to the user
		this.elements.sort(new Comparator<Element>(){
			@Override
			public int compare(Element o1, Element o2) {
				return o1.compareTo(o2);
			}
		});
	}
	public Station.Delegate getDelegate() {
		return station;
	}
	public List<Element> getElements() {
		return elements;
	}
}
