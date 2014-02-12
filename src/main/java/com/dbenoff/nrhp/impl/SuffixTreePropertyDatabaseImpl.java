package com.dbenoff.nrhp.impl;

import com.dbenoff.text.Node;
import com.dbenoff.text.impl.GeneralizedSuffixTreeImpl;
import com.dbenoff.text.impl.TextValue;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.itasoftware.nrhp.Property;
import com.itasoftware.nrhp.PropertyDatabase;
import com.itasoftware.nrhp.SearchResult;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SuffixTreePropertyDatabaseImpl implements PropertyDatabase {

	Logger log = Logger.getLogger(this.getClass());
	
	private static final long serialVersionUID = 3073407837916751301L;

	private int maxSearchResults;
	private GeneralizedSuffixTreeImpl tree;
	private String[] stringArray;
	private Multimap<String, Property> propertyMap = HashMultimap.<String, Property>create();

	private static String NAME = "name";
	private static String ADDRESS = "address"; 
	private static String CITY = "city";
	private static String STATE = "state";
	private static String PROPERTY = "property";
	private static String PROPERTIES = "properties";
	private static String PROPERTY_ID = "id";

	@Override
	public int getMaxSearchResults() {
		return maxSearchResults;
	}

	@Override
	public void initialize(File file) throws Exception {
		List<Property> propertyList = new ArrayList<Property>();

		try {
			FileInputStream fin = new FileInputStream(file);
			GZIPInputStream gzis = new GZIPInputStream(fin);

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(gzis);

			int inHeader = 0;
			PropertyImpl prop = null;
			String currentProperty = null;
			String currentValue = null;
			for (int event = parser.next();  
			event != XMLStreamConstants.END_DOCUMENT;
			event = parser.next()) {
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if (parser.getLocalName().equals(PROPERTIES)) {
						inHeader++;
					}else if (parser.getLocalName().equals(PROPERTY)) {
						prop = new PropertyImpl();
						prop.setId(parser.getAttributeValue(null, PROPERTY_ID));
					}else if (parser.getLocalName().equals(NAME)) {
						currentProperty = NAME;
					}else if (parser.getLocalName().equals(ADDRESS)) {
						currentProperty = ADDRESS;
					}else if (parser.getLocalName().equals(CITY)) {
						currentProperty = CITY;
					}else if (parser.getLocalName().equals(STATE)) {
						currentProperty = STATE;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (parser.getLocalName().equals(PROPERTIES)) {
						inHeader--;
					}else if (parser.getLocalName().equals(PROPERTY)) {
						propertyList.add(prop);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					if (inHeader > 0)  currentValue = parser.getText();
					if(currentProperty.equals(NAME)){
						prop.addName(currentValue);
					}else if(currentProperty.equals(ADDRESS)){
						prop.setAddress(currentValue);
					}else if(currentProperty.equals(CITY)){
						prop.setCity(currentValue);
					}else if(currentProperty.equals(STATE)){
						prop.setState(currentValue);
					}   
					break;
				}
			}
			parser.close();
		}
		catch (XMLStreamException ex) {
			log.warn(ex);
		}
		catch (IOException ex) {
			log.warn("IOException while parsing " + file);
		}
		
		int propIndex = 0;
		Set<String> textSet = new TreeSet<String>();
		for(Property prop : propertyList){
			String content = prop.toString();
			content = content.replaceAll("[^a-zA-Z0-9|]", "").toLowerCase();
			String[] parts = content.split("\\|");
			for(int i = 0; i < parts.length; i++){
				String word = parts[i];
				word = word.replaceAll("[\\s]", "");
				textSet.add(word);
				this.propertyMap.put(word, prop);
			}
			propIndex++;
			int mod = propIndex % 1000;
			if (mod == 0) {
				log.info("properties parsed: " + propIndex);
			}
		}
		
		tree = new GeneralizedSuffixTreeImpl();
		stringArray = textSet.toArray(new String[0]);
		tree.initialize(stringArray, propertyMap);
		
	}

	@Override
	public SearchResult search(String input) {
		return search(input, 0);
	}

	@Override
	public SearchResult search(String input, int page) {
		input = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		Set<Property> properties = new HashSet<Property>();
		long start = System.nanoTime();
		Node<TextValue> node = tree.find(input);
		long end = System.nanoTime();
		assembleResults(properties, node, getMaxSearchResults(), page);
		Property[] resultArray = new TreeSet<Property>(properties).toArray(new PropertyImpl[0]);
		DecimalFormat df = new DecimalFormat("#.####");
		
		return new SearchResultImpl(resultArray , node.getRecordCount(), df.format(new Double((end-start)/ 1000000D)) + " ms");
	}
	
	/**
	 * Walks the tree recursively from the given node and assembles a set of search results 
	 * up to the pageSize limit
	 * @param properties list to hold our search results
	 * @param node root of the subtree we are walking down to find results
	 * @param pageSize max number of results to return per request
	 * @param page page of results to return
	 */
	private void assembleResults(Set<Property> properties, Node<TextValue> node, int pageSize, int page){
		int offset = pageSize * page;
		int limit = offset + pageSize;
		List<Node<TextValue>> results = node.gatherLeaves(limit);
		for(Node<TextValue> result : results){
			Set<Property> props = (Set<Property>) this.propertyMap.get(this.stringArray[result.getNodeValue().getIndex()]);
			for(Property prop : props){
				if(offset > 0){
					offset--;
				}else{
					properties.add(prop);
					if(properties.size() == page)
						break;
				}
			}
		}
	}

	@Override
	public void setMaxSearchResults(int maxSearchResults) {
		this.maxSearchResults = maxSearchResults;
	}

}
