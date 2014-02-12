package com.dbenoff.text;

import java.util.List;

public interface Node <T> {

	public T getNodeValue();

	public void setNodeValue(T value);
	
	public Node<T> getParent();
	
	public void setParent(Node<T> parent);
	
	public List<Node<T>> getChildren();
	
	public void setChildren(List<Node<T>> children);
	
	public List<Node<T>> gatherLeaves(int recordLimit);
	
	public int getRecordCount();
	
}
