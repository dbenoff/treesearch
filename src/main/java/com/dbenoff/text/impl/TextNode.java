package com.dbenoff.text.impl;

import java.util.ArrayList;
import java.util.List;

import com.dbenoff.text.Node;
public final class TextNode implements Node<TextValue>{

	private int recordCount;
	private TextValue nodeValue;
	private Node<TextValue> parent;
	private List<Node<TextValue>> children = null;


	public TextValue getNodeValue() {
		return nodeValue;
	}
	public void setNodeValue(TextValue nodeValue) {
		this.nodeValue = nodeValue;
	}
	public Node<TextValue> getParent() {
		return parent;
	}
	public void setParent(Node<TextValue> parent) {
		this.parent = parent;
	}
	public List<Node<TextValue>> getChildren() {
		return children;
	}
	public void setChildren(List<Node<TextValue>> children) {
		this.children = children;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount ;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(this.getNodeValue() != null &&
				this.getNodeValue().getText() != null){
			Node<TextValue> parent = this.getParent();
			while(parent != null){
				sb.append("\t");
				parent = parent.getParent();
			}
			sb.append(this.getNodeValue().getText());
			sb.append(" " + this.getNodeValue().getIndex());
		}
		return sb.toString();
	}


	/**
	 * @param recordLimit the max number of properties the nodes returned should be associated with
	 * @return a list of TextNodes
	 */
	public List<Node<TextValue>> gatherLeaves(int recordLimit) {
		final List<Node<TextValue>> result = new ArrayList<Node<TextValue>>();
		gatherLeaves(result, recordLimit);
		return result;
	}

	private final void gatherLeaves(List<Node<TextValue>> result, int recordLimit) {
		if (this.children == null || this.children.size() < 1){
			result.add(this);
		} else {
			for (Node<TextValue> child : children) {
				((TextNode) child).gatherLeaves(result, recordLimit);
				if(result.size() == recordLimit)
					return;
			}
		}
	}


	public void dumpGraph(){
		dumpGraph(this);
	}

	private void dumpGraph(Node<TextValue> node){
		System.out.println(node.toString());
		if(node.getChildren() != null)
			for(Node<TextValue> child : node.getChildren()){
				dumpGraph(child);
			}
	}
}

