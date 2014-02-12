package com.dbenoff.text.impl;

import com.dbenoff.text.Node;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.itasoftware.nrhp.Property;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GeneralizedSuffixTreeImpl {
	
	private static String LINE_TERMINATOR = "$";
	Logger log = Logger.getLogger(this.getClass());
	private TextNode root;
	
	public GeneralizedSuffixTreeImpl(){
		root = new TextNode();
	}
	
	public int getSize(){
		return root.getRecordCount();
	}
	
	public void dumpGraph(){
		this.root.dumpGraph();
	}
	
	public void initialize(String[] textArray, Multimap<String, Property> propertyMap){
		log.info("textArray length: " + textArray.length);
		for(int i = 0; i < textArray.length; i++){
			final String text = textArray[i];
			this.insert(text, i);
			int mod = i % 1000;
			if (mod == 0) {
				log.info("attributes suffixed: " + i);
			}
		}
		log.info("computing record counts");
		setRecordCounts(root, textArray, propertyMap);
	}
	
	private static void setRecordCounts(Node<TextValue> node, String[] textArray, Multimap<String, Property> propertyMap){
		List<Node<TextValue>> leaves = node.gatherLeaves(Integer.MAX_VALUE);
		Set<Property> props = new HashSet<Property>();
		for(Node<TextValue> leaf : leaves){
			String key = textArray[leaf.getNodeValue().getIndex()];
			props.addAll((Set<Property>)propertyMap.get(key));
		}
		
		if(node instanceof TextNode){
			((TextNode)node).setRecordCount(props.size());
		}
		
		if(node.getChildren() != null){
			for(Node<TextValue> child : node.getChildren()){
				setRecordCounts(child, textArray, propertyMap);
			}
		}
	}
	
	private static String getMatchingPrefix(String text, String otherText){
		String matchString = "";
		int limit = text.length() < otherText.length() ? text.length() : otherText.length();
		for(int i = 0; i < limit; i++){
			if(text.charAt(i) == otherText.charAt(i)){
				matchString += text.charAt(i);
			}else{
				return matchString;
			}
		}
		return matchString;
	}
	
	public Node<TextValue> find(String search){
		return this.find(root, search);
	}

	  
	  private Node<TextValue> find(Node<TextValue> node, String search){
		  for(Node<TextValue> child : node.getChildren()){
			  String key = child.getNodeValue().getText();
			  if(search.equals(key) || key.startsWith(search)){
				  return child;
			  }
			  if(search.startsWith(key)){
				  return find(child, search.substring(key.length()));
			  }
		  }
		  return null;
	  }
	
	
	public void insert(String text, int originIndex){
		for(int i = 0; i < text.length(); i++){
			final String addText = text.substring(i) + LINE_TERMINATOR;
			addText(root, addText, originIndex);
		}
	}
	
	
	private void addText(Node<TextValue> node, String text, int index){
		if(!text.contains(LINE_TERMINATOR)){
			/*a suffix is being pushed down from a parent,
			insert it into the hierarchy*/
			Node<TextValue> newNode = new TextNode();
			TextValue nodeValue = new TextValue();
			nodeValue.setIndex(index);
			nodeValue.setText(text);
			newNode.setNodeValue(nodeValue);
			newNode.setParent(node);
			newNode.setChildren(node.getChildren());
			for(Node<TextValue> child : newNode.getChildren()){
				child.setParent(newNode);
			}
			node.setChildren(new LinkedList<Node<TextValue>>());
			node.getChildren().add(newNode);
			return;
		}
		
		if(node.getChildren() != null){
			String match = "";
			for(Node<TextValue> child : node.getChildren()){
				String childText = child.getNodeValue().getText(); 
				if(child.getNodeValue().getText().equals(text)){
					if(childText.endsWith(LINE_TERMINATOR)
							&& text.endsWith(LINE_TERMINATOR)){
						
						/*substring exists already
						split the node, add two terminators beneath*/
						child.getNodeValue().setText(childText.substring(0, childText.length() - 1));
						
						TextNode newNode = new TextNode();
						TextValue nodeValue = new TextValue();
						nodeValue.setText(LINE_TERMINATOR);
						newNode.setNodeValue(nodeValue);
						newNode.getNodeValue().setIndex(index);
						child.setChildren(new LinkedList<Node<TextValue>>());
						child.getChildren().add(newNode);
						newNode.setParent(child);
						
						newNode = new TextNode();
						nodeValue = new TextValue();
						nodeValue.setText(LINE_TERMINATOR);
						newNode.setNodeValue(nodeValue);
						newNode.getNodeValue().setIndex(child.getNodeValue().getIndex());
						child.getChildren().add(newNode);
						newNode.setParent(child);
					}
					return;
				}
				match = getMatchingPrefix(text, child.getNodeValue().getText());
				if(match.length() > 0){
					
					String textSuffix = text.substring(match.length());
					String childSuffix = child.getNodeValue().getText().substring(match.length());
					
					if(childSuffix.length() > 0){
						/*push the existing child suffix down, and transfer the index,
						the new node will be its parent*/
						addText(child, childSuffix, child.getNodeValue().getIndex());
					}else if(textSuffix.equals(LINE_TERMINATOR)){
						/*already two or more exact matches for this substring,
						add another terminator node beneath the current child*/
						TextNode newNode = new TextNode();
						TextValue nodeValue = new TextValue();
						nodeValue.setText(LINE_TERMINATOR);
						newNode.setNodeValue(nodeValue);
						newNode.getNodeValue().setIndex(index);
						child.getChildren().add(newNode);
						newNode.setParent(child);
						return;
					}
					child.getNodeValue().setText(match);
					addText(child, textSuffix, index);
					return;
				}
			}
		}
		if(text.length() > 0){
			//no matches of any kind, add a new child
			TextNode newNode = new TextNode();
			TextValue nodeValue = new TextValue();
			nodeValue.setText(text);
			newNode.setNodeValue(nodeValue);
			newNode.getNodeValue().setIndex(index);
			if(node.getChildren() == null)
				node.setChildren(new LinkedList<Node<TextValue>>());
			node.getChildren().add(newNode);
			newNode.setParent(node);
		}
	}
	


	@Test
	public void sampleUsage() {
		GeneralizedSuffixTreeImpl tree = new GeneralizedSuffixTreeImpl();
		String[] textArray = new String[3];
		textArray[0] = ("alakeviewave");
		textArray[1] = ("blakeviewave");
		textArray[2] = ("clakeviewavec");
		tree.initialize(textArray, HashMultimap.<String, Property>create());
		tree.dumpGraph();
		Node<TextValue> result = tree.find("lakeview");
		List<Node<TextValue>> results = result.gatherLeaves(10000);
		for(Node<TextValue> record : results){
			System.out.println(textArray[record.getNodeValue().getIndex()]);
		}
	}
	
}
