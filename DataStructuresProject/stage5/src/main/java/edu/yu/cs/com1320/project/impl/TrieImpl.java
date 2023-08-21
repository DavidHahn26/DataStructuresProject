package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<V> implements Trie<V> {
    class Node<V>{
        HashSet<V> values = new HashSet<>();
        Node[] links = new Node[TrieImpl.alphabetSize];
        public Node(){

        }
        public Node(V v){
            this.values.add(v);
        }
    }

    private static final int alphabetSize = 62;
    //numbers 0-9 as given
    //Capital letters are 10-35 their char value-55 - A is 65
    //Lowercase letters are 36-62 their char value-61 - a is 97

    private Node root;
    public TrieImpl(){
        this.root = new Node();
    }

    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, V val) {
        if(key == null){
            throw new IllegalArgumentException();
        }
        if(val == null){
            return;
        }else{
            //key = key.replaceAll("[^a-zA-Z0-9]", "");
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, V val, int d) {
        //create a new node
        if (x == null) {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length()) {
            x.values.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        int index = getCharIndex(c);
        x.links[index] = this.put(x.links[index], key, val, d + 1);
        return x;
    }

    private Node get(Node x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        int index = getCharIndex(c);
        return get(x.links[index], key, d + 1);
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<V> getAllSorted(String key, Comparator<V> comparator) {
        if(key == null){
            throw new IllegalArgumentException();
        }
        Node x = this.get(this.root, key, 0);
        if(x == null){
            return new ArrayList<>();
        }
        ArrayList<V> vals = new ArrayList<>(x.values);
        vals.sort(comparator);
        return vals;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<V> getAllWithPrefixSorted(String prefix, Comparator<V> comparator) {
        if(prefix == null){
            throw new IllegalArgumentException();
        }
        Node pre = this.get(this.root, prefix, 0);
        List<V> results = new ArrayList<>();
        if(pre == null){
            return results;
        }
        getAllByPrefix(pre, results);
        Set<V> temp = new HashSet<>();
        temp.addAll(results);
        List<V> uniqueResults = new ArrayList<>();
        uniqueResults.addAll(temp);
        uniqueResults.sort(comparator);
        return uniqueResults;/*
        results.sort(comparator);
        return results;*/
    }

    private void getAllByPrefix(Node x, List<V> results){
        if(!x.values.isEmpty()){
            results.addAll(x.values);
        }
        for(int i = 0; i< alphabetSize; i++){
            if(x.links[i] != null){
                getAllByPrefix(x.links[i], results);
            }
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<V> deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException();
        }
        Node x = get(this.root, prefix, 0);
        if(x == null){
            return new HashSet<>();
        }
        Node preparent = get(this.root, prefix.substring(0, prefix.length()-1), 0);
        ArrayList<V> vals = new ArrayList<>();
        getAllByPrefix(x, vals);
        preparent.links[getCharIndex(prefix.charAt(prefix.length()-1))] = null;
        return new HashSet<>(vals);
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - set the val to empty set
        if (d == key.length()) {
            x.values.clear();
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            int index = getCharIndex(c);
            x.links[index] = this.deleteAll(x.links[index], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (!x.values.isEmpty()) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<V> deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException();
        }
        Node x = this.get(this.root, key, 0);
        if(x == null){
            return new HashSet<>();
        }
        Set<V> vals = new HashSet<>();
        for(Object val : x.values){
            vals.add((V)val);
        }
        //System.out.println(vals.toString());
        //now do deletions for all other nodes
        deleteAll(this.root, key, 0);
        return vals;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public V delete(String key, V val) {
        if(key == null || val == null){
            throw new IllegalArgumentException();
        }
        Node x = get(this.root, key, 0);
        if(x == null || !x.values.contains(val)){
            return null;
        }
        if(x.values.size() > 1){
            x.values.remove(val);
        }else{
            //complicated case, only one, need to delete node
            deleteAll(key);
        }
        return val;
    }


    private int getCharIndex(char c){
        //numbers 0-9 as given
        //Capital letters are 10-35 their char value-55 - A is 65
        //Lowercase letters are 36-62 their char value-61 - a is 97
        if(c >= 48 && c <= 57){
            return c-48;
        }else if(c >= 65 && c <= 90){
            return c-55;
        }else if(c >= 97 && c <= 122){
            return c - 61;
        }
        throw new IllegalArgumentException();
    }
}