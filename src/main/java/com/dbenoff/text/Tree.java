package com.dbenoff.text;

public interface Tree<T> {

	  /**
	   * Insert a new string key and its value to the tree. 
	   * 
	   * @param key
	   *            The string key of the object
	   * @param value
	   *            The value that need to be stored corresponding to the given
	   *            key.
	   */
	  public void insert(String key, T value);
	  
	  /**
	   * Find a value based on its corresponding key.
	   * 
	   * @param key The key for which to search the tree.
	   * @return The value corresponding to the key. null if it can not find the key
	   */
	  public Node<T> find(String key);

}
