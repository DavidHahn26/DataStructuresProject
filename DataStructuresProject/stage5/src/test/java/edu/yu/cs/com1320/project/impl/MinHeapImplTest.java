package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinHeapImplTest {
    MinHeapImpl<Document> heap = new MinHeapImpl<>();
    URI[] uris = new URI[13];
    String[] strs = new String[12];
    Document[] docs = new Document[12];

    @BeforeEach
    void setup(){
        uris[0] = URI.create("foo://example.com:8042/over/there?name=ferret#nose");
        uris[1] = URI.create("foo://example.com:8042/over/there?name=ferret#eyes");
        uris[2] = URI.create("foo://example.com:8042/over/there?name=ferret#foot");
        uris[3] = URI.create("foo://example.com:8042/over/there?name=ferret#hand");
        uris[4] = URI.create("foo://example.com:8042/over/there?name=ferret#face");
        uris[5] = URI.create("foo://example.com:8042/over/there?name=ferret#leg");
        uris[6] = URI.create("foo://example.com:8042/over/there?name=ferret#arms");
        uris[7] = URI.create("foo://example.com:8042/over/there?name=ferret#lefthand");
        uris[8] = URI.create("foo://example.com:8042/over/there?name=ferret#cheek");
        uris[9] = URI.create("foo://example.com:8042/over/there?name=ferret#fingers");
        uris[10] = URI.create("foo://example.com:8042/over/there?name=ferret#toes");
        uris[11] = URI.create("foo://example.com:8042/over/there?name=ferret#snout");
        uris[12] = URI.create("foo://example.com:8042/over/there?name=ferret#belly");
        strs[0] = "This is the bytes one text for the array";
        strs[1] = "This is the bytes two text for the array";
        strs[2] = "This is the bytes three text for the array. bye bye bye";
        strs[3] = "This is the bytes four text for the array";
        strs[4] = "This is the bytes five text for the array";
        strs[5] = "This is the bytes six text for the array bytes bytes";
        strs[6] = "This is the bytes seven text for the array bytes";
        strs[7] = "This is the bytes eight text for the array";
        strs[8] = "This is the bytes nine text for the array";
        strs[9] = "This is the bytes ten text for the array";
        strs[10] = "This is the eleven text for the array pwa";
        strs[11] = "This is the bytes twelve text for the array pwaef";
        for(int i=0;i<12;i++){
            docs[i] = new DocumentImpl(uris[i], strs[i], null);
            docs[i].setLastUseTime(System.nanoTime());
            heap.insert(docs[i]);
        }
    }

    @Test
    void remove(){
        Document doc = heap.remove();
        assertEquals(doc, docs[0]);
    }

    @Test
    void reHeapify(){
        docs[10].setLastUseTime(0);
        this.heap.reHeapify(docs[10]);
        Document doc = heap.remove();
        assertEquals(doc, docs[10]);
    }
}
