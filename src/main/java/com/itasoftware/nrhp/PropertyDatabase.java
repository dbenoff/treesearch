package com.itasoftware.nrhp;

import java.io.File;
import java.io.Serializable;

/**
 * A database of NRHP properties.
 */
public interface PropertyDatabase extends Serializable {

    /**
     * @param maxSearchResults The maximum number of search results returned by #search.
     */
    void setMaxSearchResults(int maxSearchResults);
    
    /**
     * @return the maximum number of search results returned by #search.
     */
    int getMaxSearchResults();
    
    /**
     * Fully initialize the database before returning.  
     * Call this after setting the maximum number of search results.
     * @param file location of gzipped properties XML file.
     */
    void initialize(File file) throws Exception;
    
    /**
     * Find properties matching the specified input text.
     * @param input search input as typed by the user.
     */
    SearchResult search(String input);
    
    /**
     * Find properties matching the specified input text.
     * @param input search input as typed by the user.
     * @param page the page of results to return
     */
    SearchResult search(String input, int page);

}