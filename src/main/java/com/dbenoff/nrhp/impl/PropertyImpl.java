package com.dbenoff.nrhp.impl;

import com.itasoftware.nrhp.Property;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
public class PropertyImpl implements Property, Cloneable {

	private static final long serialVersionUID = -6154189016987254159L;
	private String id;
	private String address;
	private String city;
	private List<String> nameList = new ArrayList<String>();
	private String state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String[] getNames() {
		return (String[]) nameList.toArray(new String[0]);
	}

	@Override
	public String getState() {
		return state;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void addName(String name) {
		this.nameList.add(name);
	}
	
	public void setNames(String[] names) {
		this.nameList = Arrays.asList(names);
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public int compareTo(Property o) {
		return this.toString().compareTo(o.toString());
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String name : nameList){
			sb.append(name);
		}
		if(address != null) sb.append("|" + address);
		if(city != null) sb.append("|" + city);
		if(state != null) sb.append(state);
		return sb.toString();
	}

	  public Object clone() {
		    Object o = null;
		    try {
		      o = super.clone();
		    } catch (CloneNotSupportedException e) {
		      System.err.println("Property can't clone");
		    }
		    return o;
		}
	
}
