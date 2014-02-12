package com.dbenoff.ws;

import com.itasoftware.nrhp.PropertyDatabase;
import com.itasoftware.nrhp.SearchResult;
import com.dbenoff.nrhp.impl.SuffixTreePropertyDatabaseImpl;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author dbenoff
 * 
 * Singleton wrapper to hold a propertyDatabase instance 
 */
public class PropertySearchFactory {

	Logger log = Logger.getLogger(this.getClass());
	private static PropertyDatabase propertyDatabase;
	private static File file;
	private static PropertySearchFactory instance = null;
	private static int maxResults = 50;

	protected PropertySearchFactory(File file) throws Exception {
		propertyDatabase = new SuffixTreePropertyDatabaseImpl();
		propertyDatabase.setMaxSearchResults(maxResults);
		propertyDatabase.initialize(file);
	}

	public static SearchResult search(String input){
		return propertyDatabase.search(input);
	}

	public static SearchResult search(String input, int offset){
		return propertyDatabase.search(input, offset);
	}
	
	public static void init() throws Exception {
		if(instance == null) {
			instance = new PropertySearchFactory(file);
		}
	}

	public static void setFile(File infile){
		file = infile;
	}

	public static void setPageSize(int maxResults) {
		PropertySearchFactory.maxResults = maxResults;
	}	
}
