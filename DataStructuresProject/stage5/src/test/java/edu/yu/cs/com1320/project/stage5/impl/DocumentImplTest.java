package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocumentImplTest {
    @Test
    void getWords() throws URISyntaxException {
        DocumentImpl doc = new DocumentImpl(new URI("HELLOE"), " You smell".getBytes());
        Set<String> words = doc.getWords();
        Set<String> empty = new HashSet<>();
        assertEquals(words, empty);
    }

    @Test
    void getWordsCase() throws URISyntaxException {
        DocumentImpl doc = new DocumentImpl(new URI("FHI"), "hello Hello HELLO Hello", null);
        int hello = doc.wordCount("hello");
        int Hello = doc.wordCount("Hello");
        int qu = doc.wordCount("hElLo");

        assertEquals(hello, 1);
        assertEquals(Hello, 2);
        assertEquals(qu, 0);
    }
}
