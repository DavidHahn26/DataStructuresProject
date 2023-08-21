package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.impl.TrieImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TrieImplTest {
    TrieImpl<String> wordsList;

    @BeforeEach
    void setup(){
        wordsList = new TrieImpl<>();
        String paragraph = "Keys in the trie are implicitly represented by paths from the root that end at nodes with\n" +
                "non-null values. For example, the string sea is associated with the value 2 in the trie\n" +
                "because the 19th link in the root (which points to the trie for all keys that start with s)\n" +
                "is not null and the 5th link in the node that link refers to (which points to the trie for\n" +
                "all keys that start with se) is not null, and the first link in the node that link refers to\n" +
                "(which points to the trie for all keys that starts with sea) has the value 2. Neither the\n" +
                "string sea nor the characters s, e, and a are stored in the data structure. Indeed, the data\n" +
                "structure contains no characters or strings, just links and values. Since the parameter R\n" +
                "plays such a critical role, we refer to a trie for an R-character alphabet as an R-way trie.\n" +
                "With these preparations, the symbol-table implementation TrieST on the facing\n" +
                "page is straightforward. It uses recursive methods like those that we used for search\n" +
                "trees in Chapter 3, based on a private Node class with instance variable val for client\n" +
                "values and an array next[] of Node references. The methods are compact recursive\n" +
                "implementations that are worthy of careful study. Next, we discuss implementations of\n" +
                "the constructor that takes an Alphabet as argument and the methods size(), keys(),\n" +
                "longestPrefixOf(), keysWithPrefix(), keysThatMatch(), and delete(). These\n" +
                "are also easily understood recursive methods, each slightly more complicated than the\n" +
                "last.\n" +
                "Size. As for the binary search trees of Chapter 3, three straightforward options are\n" +
                "available for implementing size():\n" +
                "An eager implementation where we maintain the number of keys in an instance\n" +
                "variable N.\n" +
                "A very eager implementation where we maintain the number of keys in a subtrie\n" +
                "as a node instance variable that we update after the recursive calls in put() and\n" +
                "delete().\n" +
                "A lazy recursive implementation like\n" +
                "the one at right. It traverses all of the\n" +
                "nodes in the trie, counting the number\n" +
                "having a non-null value.\n" +
                "As with binary search trees, the lazy implementation is instructive but should be\n" +
                "avoided because it can lead to performance\n" +
                "problems for clients. The eager implementations are explored in the exercises";
        paragraph = paragraph.replaceAll("[^a-zA-Z0-9 ]", "");
        String[] words = paragraph.split(" ");
        for(String word : words){
            wordsList.put(word, word+(Math.round(Math.random()*10000)));
        }
    }

    @Test
    void getAllTest(){
        List<String> allWords = wordsList.getAllSorted("the", (o1, o2) -> isAlphabetized(o1, o2, 0));
        System.out.println(allWords.toString());
        System.out.println(allWords.size());
    }

    @Test
    void getAllByPrefixTest(){
        List<String> allWords = wordsList.getAllWithPrefixSorted("th", (o1, o2) -> isAlphabetized(o1, o2, 0));
        System.out.println(allWords.toString());
        System.out.println(allWords.size());
    }

    @Test
    void deleteAllTest(){
        wordsList.deleteAll("the");
        System.out.println(wordsList.getAllWithPrefixSorted("th", (o1, o2)->isAlphabetized(o1, o2, 0)));
    }

    @Test
    void deleteAllWithPrefixTest(){
        wordsList.deleteAllWithPrefix("the");
        System.out.println(wordsList.getAllWithPrefixSorted("the", (o1, o2)->isAlphabetized(o1, o2, 0)));
    }

    private int isAlphabetized(String a, String b, int index){
        if(a.equals(b)){
            return 0;
        }
        if(a.charAt(index) > b.charAt(index)){
            return 1;
        }else if(a.charAt(index) < b.charAt(index)){
            return -1;
        }else{
            if(index == a.length()-1 || index == b.length()-1){
                if(a.length() > b.length()){
                    return -1;
                }else{
                    return 1;
                }
            }else{
                return isAlphabetized(a, b, index+1);
            }
        }
    }
}
