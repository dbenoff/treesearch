package com.dbenoff.nrhp.impl;

import com.dbenoff.text.Node;
import com.dbenoff.text.impl.TextNode;
import com.dbenoff.text.impl.TextValue;
import com.sun.deploy.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ScopelyTreeExercise {
    Logger log = Logger.getLogger(this.getClass());

    public static void main(String[] args){
        ScopelyTreeExercise maker =  new ScopelyTreeExercise();
        maker.run();
    }

    public void run(){
        createTreeFromPath();
        createMultipleLeavesTreeFromPath();
        createCombinatorialNodes();
        buildPath();
    }

    /**
     * Part 1: Insert into Tree
     Write a function to build a tree out of a path structure, such as /home/sports/basketball/ncaa/,
     and insert an new path into the tree. For example, if you inserted /home/music/rap/gangster into
     the tree, it would add a leaf node to rap.
     */
    public void createTreeFromPath(){
        List<String> pathElements = new ArrayList(Arrays.asList("home/sports/basketball/ncaa".split("\\/")));
        TextNode root = new TextNode();
        insertFromPath(root, pathElements);
        pathElements = new ArrayList(Arrays.asList("home/music/rap/gangster".split("\\/")));
        insertFromPath(root, pathElements);
        root.dumpGraphSimplified();
    }

    /**
     * Part 2: Support Dual Leafnode inserts
     Write a function that allows you to insert two leafnodes
     at the same time. For example, given
     the path /home/sports/football/NFL|NCAA it would insert *two* leaf nodes: NFL, and NCAA. Note
     that the pipe character, “|”, marks the node boundaries.
     */
    public void createMultipleLeavesTreeFromPath(){
        List<String> pathElements = new ArrayList(Arrays.asList("home/sports/football/NFL|NCAA".split("\\/")));
        TextNode root = new TextNode();
        insertFromPath(root, pathElements);
        root.dumpGraphSimplified();
    }


    /**
     * Part 3: Support a combinatorial leafnode
     insert
     Write a function that allows you to insert a the combinatorial explosion of a path into a leafnode.
     For example, given the path /home/music/rap|rock|pop, it would insert the following leaf nodes:
     */

    /**
     * Part 4: Support combinatorial nodes at any level
     Write a function that allows you to insert a combinatorial explosion of a path at any level. For
     example, give the path /home/sports|music/misc|favorites, it would create the following tree:
     */
    public TextNode createCombinatorialNodes(){
        //List<String> pathElements = new ArrayList(Arrays.asList("home/music/rap|rock|pop".split("\\/")));  question 4
        List<String> pathElements = new ArrayList(Arrays.asList("home/sports|music/misc|favorites".split("\\/")));     //question 5
        List<String> expandedPathElements = new ArrayList<String>();

        for(int i = 0; i < pathElements.size(); i++){
            String pathElement = pathElements.get(i);
            if(pathElement.contains("|")){
                List<List<String>> powerSet =powerset(new ArrayList(Arrays.asList(pathElement.split("\\|"))));
                List<String> powerSetStrings = new ArrayList<String>();
                for(List<String> subList : powerSet){
                    powerSetStrings.add(StringUtils.join(subList, "-"));
                }
                expandedPathElements.add(StringUtils.join(powerSetStrings, "|"));
            }else{
                expandedPathElements.add(pathElement);
            }
        }
        TextNode root = new TextNode();
        insertFromPath(root, expandedPathElements);
        root.dumpGraphSimplified();
        return root;
    }

    /**
     * Write a function that takes as input a Tree and outputs a combinatorial tree. For example, in the
     Tree displayed in Part 4, the output of the function would be: /home/sports|music/misc|favorites.
     */
    public void buildPath(){
        TextNode node = createCombinatorialNodes();
        pruneTree(node);
        node.dumpGraphSimplified();
    }

    public void pruneTree(Node<TextValue> node){
        //find the child whose text includes the text of all its siblings, then prune the siblings
        if(!CollectionUtils.isEmpty(node.getChildren())){
            Node<TextValue> comboNode = null;
            for(Node<TextValue> child : node.getChildren()){
                boolean foundComboNode = true;
                for(Node<TextValue> sibling : node.getChildren()){
                    if(!child.getNodeValue().getText().contains(sibling.getNodeValue().getText())){
                        foundComboNode = false;
                    }
                }
                if(foundComboNode){
                    comboNode = child;
                    break;
                }
            }
            node.getChildren().clear();
            node.getChildren().add(comboNode);
            pruneTree(comboNode);
        }else{
            return;
        }
    }

    public <T> List<List<T>> powerset(Collection<T> list) {
        List<List<T>> ps = new ArrayList<List<T>>();
        ps.add(new ArrayList<T>());   // add the empty set

        // for every item in the original list
        for (T item : list) {
            List<List<T>> newPs = new ArrayList<List<T>>();

            for (List<T> subset : ps) {
                // copy all of the current powerset's subsets
                newPs.add(subset);

                // plus the subsets appended with the current item
                List<T> newSubset = new ArrayList<T>(subset);
                newSubset.add(item);
                newPs.add(newSubset);
            }

            // powerset is now powerset of list.subList(0, list.indexOf(item)+1)
            ps = newPs;
        }
        return ps;
    }



    private void insertFromPath(Node<TextValue> currentNode, List<String> path){
        if(path.size() == 0)
            return;
        String currentElement = path.get(0);
        path.remove(0);
        List<String> leaves = new ArrayList(Arrays.asList(currentElement.split("\\|")));
        for(String leaf : leaves){
            List<String> subPath = new ArrayList<String>();
            subPath.addAll(path);
            if(currentNode.getChildren() != null){
                for(Node<TextValue> child : currentNode.getChildren()){
                    if(child.getNodeValue().getText().equals(leaf)){
                        insertFromPath(child, subPath);
                    }
                }
            }else{
                currentNode.setChildren(new ArrayList<Node<TextValue>>());
            }
            TextNode child = new TextNode();
            child.setParent(currentNode);
            child.setNodeValue(new TextValue());
            child.getNodeValue().setText(leaf);
            currentNode.getChildren().add(child);
            insertFromPath(child, subPath);
        }
    }
}
