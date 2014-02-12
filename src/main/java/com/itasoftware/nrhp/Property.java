package com.itasoftware.nrhp;

import java.io.Serializable;

/**
 * Information about a property.
 */
public interface Property extends Comparable<Property>, Serializable {

    String getAddress();
    
    String getCity();

    String[] getNames();
    
    String getState();
    
}
