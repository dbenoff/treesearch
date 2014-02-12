package com.dbenoff.nrhp.impl;

import com.itasoftware.nrhp.Property;
import com.itasoftware.nrhp.SearchResult;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchResultImpl implements SearchResult {

	private int totalPropertyCount;
	private Property[] properties;
	private String executionTime;
	
	@Override
	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public SearchResultImpl(Property[] properties, int totalPropertyCount, String executionTime){
		this.properties = properties;
		this.totalPropertyCount = totalPropertyCount;
		this.executionTime = executionTime;
	}
	
	@Override
	public Property[] getProperties() {
		return properties;
	}

	@Override
	public int getTotalPropertyCount() {
		return totalPropertyCount;
	}

}
