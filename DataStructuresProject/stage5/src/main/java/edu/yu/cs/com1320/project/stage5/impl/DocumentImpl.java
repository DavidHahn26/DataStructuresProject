package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{
    private String text;
    private byte[] binary;
    private URI uri;
    private Map<String, Integer> allWords;
    private long lastUse;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        this.uri = uri;
        this.text = txt;
        this.binary = null;
        if(wordCountMap == null) {
            this.allWords = new HashMap<>();
            String text = txt.replaceAll("[^a-zA-Z0-9 ]", "");
            String[] words = text.split(" ");
            for (String word : words) {
                this.allWords.merge(word, 1, Integer::sum);
            }
        }else{
            this.allWords = wordCountMap;
        }
        this.lastUse = 0;
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        this.uri = uri;
        this.binary = binaryData;
        this.text = null;
        this.lastUse = 0;
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return this.text;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData() {
        return this.binary;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        if(this.text == null){
            return 0;
        }
        if(this.allWords.get(word) != null){
            return this.allWords.get(word);
        }else{
            return 0;
        }
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        return this.text != null ? allWords.keySet() : new HashSet<>();
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime() {
        if(this.lastUse != 0){
            return this.lastUse;
        }else{
            return -1;
        }
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        //if(valid time), maybe add this
        this.lastUse = timeInNanoseconds;
    }

    /**
     * @return a copy of the word to count map, so it can be serialized
     */
    @Override
    public Map<String, Integer> getWordMap() {
        return this.allWords == null ? null : this.allWords;
    }

    /**
     * This must set the word to count map during deserialization
     *
     * @param wordMap
     */
    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.allWords = wordMap;
    }

    @Override
    public int hashCode() {
        return Math.abs(Utils.calculateHashCode(this.uri, this.text, this.binary));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(obj instanceof DocumentImpl){
            return ((DocumentImpl) obj).hashCode() == this.hashCode();
        }else{
            return false;
        }
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Document o) {
        if(o == null){
            throw new NullPointerException();
        }
        if (this.lastUse < o.getLastUseTime()) {
            return -1;
        } else if (this.lastUse > o.getLastUseTime()) {
            return 1;
        } else {
            return 0;
        }
    }
}
