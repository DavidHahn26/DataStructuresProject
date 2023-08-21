package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.BINARY;
import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {
    DocumentStoreImpl store;
    DocumentPersistenceManager dp = new DocumentPersistenceManager(null);
    URI uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9, uri10, uri11, uri12, uri13;
    byte[] bytes1, bytes2, bytes3, bytes4, bytes5, bytes6, bytes7, bytes8, bytes9, bytes10, bytes11, bytes12;

    @BeforeEach
    void setup() throws IOException{
        uri1 = URI.create("foo://example.com/over/there/name/ferret/u1");
        uri2 = URI.create("foo://example.com/over/there/name/ferret/u2");
        uri3 = URI.create("foo://example.com/over/there/name/ferret/u3");
        uri4 = URI.create("foo://example.com/over/there/name/ferret/u4");
        uri5 = URI.create("foo://example.com/over/there/name/ferret/u5");
        uri6 = URI.create("foo://example.com/over/there/name/ferret/u6");
        uri7 = URI.create("foo://example.com/over/there/name/ferret/u7");
        uri8 = URI.create("foo://example.com/over/there/name/ferret/u8");
        uri9 = URI.create("foo://example.com/over/there/name/ferret/u9");
        uri10 = URI.create("foo://example.com/over/there/name/ferret/u10");
        uri11 = URI.create("foo://example.com/over/there/name/ferret/u11");
        uri12 = URI.create("foo://example.com/over/there/name/ferret/u12");
        uri13 = URI.create("foo://example.com/over/there/name/ferret/u13");
        bytes1 = "This is the bytes one text for the array".getBytes();
        bytes2 = "This is the bytes two text for the array".getBytes();
        bytes3 = "This is the bytes three text for the array. bye bye bye".getBytes();
        bytes4 = "This is the bytes four text for the array".getBytes();
        bytes5 = "This is the bytes five text for the array".getBytes();
        bytes6 = "This is the bytes six text for the array bytes bytes".getBytes();
        bytes7 = "This is the bytes seven text for the array bytes".getBytes();
        bytes8 = "This is the bytes eight text for the array".getBytes();
        bytes9 = "This is the bytes nine text for the array".getBytes();
        bytes10 = "This is the bytes ten text for the array".getBytes();
        bytes11 = "This is the eleven text for the array pwa".getBytes();
        bytes12 = "This is the bytes twelve text for the array pwaef".getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(bytes1);
        ByteArrayInputStream stream2 = new ByteArrayInputStream(bytes2);
        ByteArrayInputStream stream3 = new ByteArrayInputStream(bytes3);
        ByteArrayInputStream stream4 = new ByteArrayInputStream(bytes4);
        ByteArrayInputStream stream5 = new ByteArrayInputStream(bytes5);
        ByteArrayInputStream stream6 = new ByteArrayInputStream(bytes6);
        ByteArrayInputStream stream7 = new ByteArrayInputStream(bytes7);
        ByteArrayInputStream stream8 = new ByteArrayInputStream(bytes8);
        ByteArrayInputStream stream9 = new ByteArrayInputStream(bytes9);
        ByteArrayInputStream stream10 = new ByteArrayInputStream(bytes10);
        ByteArrayInputStream stream11 = new ByteArrayInputStream(bytes11);
        ByteArrayInputStream stream12 = new ByteArrayInputStream(bytes12);
        //store = new DocumentStoreImpl(new File("C:/Users/shimm/Documents/school/spring 2023/data structures/"));
        store = new DocumentStoreImpl();
        store.put(stream1, uri1, TXT);
        store.put(stream2, uri2, TXT);
        store.put(stream3, uri3, TXT);
        store.put(stream4, uri4, TXT);
        store.put(stream5, uri5, TXT);
        store.put(stream6, uri6, TXT);
        store.put(stream7, uri7, TXT);
        store.put(stream8, uri8, TXT);
        store.put(stream9, uri9, TXT);
        store.put(stream10, uri10, TXT);
        store.put(stream11, uri11, TXT);
        store.put(stream12, uri12, TXT);
    }

    /*@AfterAll
    static void cleanup() {
        DocumentStoreImpl dstore = new DocumentStoreImpl();
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/nose"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/eyes"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/foot"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/hand"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/face"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/leg"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/arms"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/lefthand"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/cheek"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/fingers"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/toes"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/snout"));
        dstore.delete(URI.create("foo://example.com/over/there/name/ferret/belly"));
    }*/
    @AfterEach
    void cleanup() throws IOException {
        store.setMaxDocumentCount(Integer.MAX_VALUE);
        store.setMaxDocumentBytes(Integer.MAX_VALUE);
        store.get(uri1);
        store.get(uri2);
        store.get(uri3);
        store.get(uri4);
        store.get(uri5);
        store.get(uri6);
        store.get(uri7);
        store.get(uri8);
        store.get(uri9);
        store.get(uri10);
        store.get(uri11);
        store.get(uri12);
        /*store.delete(uri1);
        store.delete(uri2);
        store.delete(uri3);
        store.delete(uri4);
        store.delete(uri5);
        store.delete(uri6);
        store.delete(uri7);
        store.delete(uri8);
        store.delete(uri9);
        store.delete(uri10);
        store.delete(uri11);
        store.delete(uri12);*/

    }

    @Test
    void getTest(){
        String result1 = store.get(uri9).getDocumentTxt();
        String result2 = store.get(uri7).getDocumentTxt();
        String result3 = store.get(uri11).getDocumentTxt();
        String result4 = store.get(uri10).getDocumentTxt();
        assertEquals(new String(bytes9, StandardCharsets.UTF_8), result1);
        assertEquals(new String(bytes7, StandardCharsets.UTF_8), result2);
        assertEquals(new String(bytes11, StandardCharsets.UTF_8), result3);
        assertEquals(new String(bytes10, StandardCharsets.UTF_8), result4);
    }

    @Test
    void deleteNull() throws IOException {
        int result = store.put(null, uri13, TXT);
        assertEquals(result, 0);
    }

    @Test
    void putDeleteTest() throws IOException {
        String og = new String(bytes3, StandardCharsets.UTF_8);
        String returned = store.get(uri3).getDocumentTxt();
        assertEquals(og, returned);
        store.put(null, uri3, TXT);
        Document doc = store.get(uri3);
        assertNull(doc);
    }

    @Test
    void deleteTest() {
        String og = new String(bytes2, StandardCharsets.UTF_8);
        String returned = store.get(uri2).getDocumentTxt();
        assertEquals(og, returned);
        store.delete(uri2);
        Document doc = store.get(uri2);
        assertNull(doc);
    }

    @Test
    void putAddTest() throws IOException {
        URI uri = URI.create("foo://example.com/over/there/name/ferret/nose");
        byte[] words = "Test message for putAddTest".getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(words);
        store.put(stream, uri, BINARY);
        byte[] get = store.get(uri).getDocumentBinaryData();
        assertArrayEquals(words, get);
    }

    @Test
    void putChangeValueTest() throws IOException {
        String bytes1old = "This is the bytes one text for the array";
        String bytes1new = "Now this is the new bytes one text for the array";
        ByteArrayInputStream stream1 = new ByteArrayInputStream(bytes1new.getBytes());
        String returnedOld = store.get(uri1).getDocumentTxt();
        assertEquals(bytes1old, returnedOld);
        store.put(stream1, uri1, TXT);
        String returnedNew = store.get(uri1).getDocumentTxt();
        assertEquals(bytes1new, returnedNew);
    }

    @Test
    void putNullKeyTest() {
        URI uri = URI.create("foo://example.com/over/there/name/ferret/nose");
        DocumentStoreImpl doc = new DocumentStoreImpl();
        byte[] words = "HELLO HO IS IR DOING".getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(words);
        assertThrows(IllegalArgumentException.class, ()->{doc.put(stream, null, TXT);});
    }

    @Test
    void bigTest() throws IOException {
        putChangeValueTest();
        putDeleteTest();
        putAddTest();
        getTest();
    }

    @Test
    void undoDelete() throws IOException {
        DocumentImpl ur5 = (DocumentImpl) store.get(uri5);
        assertNotNull(store.get(uri11));
        assertNotNull(store.get(uri3));
        assertNotNull(store.get(uri5));
        store.delete(uri11);
        store.delete(uri3);
        store.delete(uri5);
        assertNull(store.get(uri11));
        assertNull(store.get(uri3));
        assertNull(store.get(uri5));
        store.undo();
        store.undo();
        store.undo();
        assertNotNull(store.get(uri11));
        assertNotNull(store.get(uri3));
        assertNotNull(store.get(uri5));
        assertEquals(ur5, (DocumentImpl) store.get(uri5));
    }

    @Test
    void undoAdd() throws IOException {
        assertNotNull(store.get(uri12));
        store.undo();
        assertNull(store.get(uri12));
    }

    @Test
    void undoChange() throws IOException {
        assertEquals(store.get(uri10).getDocumentTxt(), new String(bytes10, StandardCharsets.UTF_8));
        InputStream streamnew = new ByteArrayInputStream("EDFIUEHFIUEHFE".getBytes());
        store.put(streamnew, uri10, TXT);
        store.undo();
        String returned = store.get(uri10).getDocumentTxt();
        assertEquals(returned, new String(bytes10, StandardCharsets.UTF_8));
    }

    @Test
    void uriUndoDelete() throws IOException {
        DocumentImpl ur5 = (DocumentImpl) store.get(uri5);
        DocumentImpl ur11 = (DocumentImpl) store.get(uri11);
        assertNotNull(store.get(uri11));
        assertNotNull(store.get(uri3));
        assertNotNull(store.get(uri5));
        store.delete(uri11);
        store.delete(uri3);
        store.delete(uri5);
        assertNull(store.get(uri11));
        assertNull(store.get(uri3));
        assertNull(store.get(uri5));
        store.undo(uri11);
        store.undo();
        assertNotNull(store.get(uri11));
        assertNotNull(store.get(uri5));
        assertEquals(ur11, (DocumentImpl) store.get(uri11));
        assertEquals(ur5, (DocumentImpl) store.get(uri5));
    }

    @Test
    void uriUndoChange() throws IOException{
        assertEquals(store.get(uri10).getDocumentTxt(), new String(bytes10, StandardCharsets.UTF_8));
        assertEquals(store.get(uri7).getDocumentTxt(), new String(bytes7, StandardCharsets.UTF_8));
        assertEquals(store.get(uri12).getDocumentTxt(), new String(bytes12, StandardCharsets.UTF_8));
        InputStream streamnew = new ByteArrayInputStream("EDFIUEHFIUEHFE".getBytes());
        store.put(streamnew, uri10, TXT);
        store.put(streamnew, uri7, TXT);
        store.put(streamnew, uri12, TXT);
        store.undo(uri10);
        String returned = store.get(uri10).getDocumentTxt();
        assertEquals(returned, new String(bytes10, StandardCharsets.UTF_8));
    }

    @Test
    void uriUndoIllegalState(){
        assertThrowsExactly(IllegalStateException.class, ()->store.undo(uri13));
    }

    @Test
    void deleteAllTest(){
        store.deleteAll("bytes");
        assertNull(store.get(uri6));
        assertNull(store.get(uri12));
        assertNull(store.get(uri2));
    }

    @Test
    void deleteAllWithPrefixTest(){
        assertNotNull(store.get(uri6));
        store.deleteAllWithPrefix("si");
        assertNull(store.get(uri6));
        assertNotNull(store.get(uri5));
        store.deleteAllWithPrefix("byt");
        assertNull(store.get(uri10));
        assertNull(store.get(uri5));
        assertNull(store.get(uri1));
    }

    @Test
    void searchTest(){
        List<Document> docs = store.search("bytes");
        //6, 7, everything else, not 11
        Document six = store.get(uri6);
        Document seven = store.get(uri7);
        Document eleven = store.get(uri11);
        Document two = store.get(uri2);
        assertEquals(docs.get(0), six);
        assertEquals(docs.get(1), seven);
        assertFalse(docs.contains(eleven));
        assertTrue(docs.contains(two));
    }

    @Test
    void searchByPrefixTest(){
        List<Document> docs = store.searchByPrefix("by");
        //3, 6, 7, everything else, not 11
        Document six = store.get(uri6);
        Document seven = store.get(uri7);
        Document eleven = store.get(uri11);
        Document two = store.get(uri2);
        Document three = store.get(uri3);
        assertEquals(docs.get(0), three);
        assertEquals(docs.get(1), six);
        assertEquals(docs.get(2), seven);
        assertFalse(docs.contains(eleven));
        assertTrue(docs.contains(two));
    }

    @Test
    void deleteAllByPrefixUndo(){
        store.deleteAll("bytes");
        assertNull(store.get(uri6));
        assertNull(store.get(uri12));
        assertNull(store.get(uri2));
        store.undo();
        assertNotNull(store.get(uri6));
        assertNotNull(store.get(uri12));
        assertNotNull(store.get(uri2));
    }

    @Test
    void deleteAllByPrefixUndoUri(){
        store.deleteAllWithPrefix("by");
        assertNull(store.get(uri6));
        assertNull(store.get(uri12));
        assertNull(store.get(uri2));
        store.undo(uri6);
        store.undo(uri12);
        assertNotNull(store.get(uri6));
        assertNotNull(store.get(uri12));
        assertNull(store.get(uri1));
    }

    @Test
    void commandStackTest(){
        store.delete(uri5);
        store.deleteAllWithPrefix("pw");
        assertNull(store.get(uri11));
        assertNull(store.get(uri12));
        assertNull(store.get(uri5));
        store.delete(uri3);
        assertNull(store.get(uri3));
        store.undo(uri12);
        store.undo(uri11);
        assertNull(store.get(uri3));
        assertNull(store.get(uri5));
        store.undo();
        assertNotNull(store.get(uri3));
        store.undo();
        assertNotNull(store.get(uri5));
    }

    @Test
    void setMaxBytes() throws IOException{
        store.setMaxDocumentBytes(100);
        store.put(new ByteArrayInputStream(bytes3), uri1, TXT);
    }

    @Test
    void setMaxDocs(){
        store.setMaxDocumentCount(2);
        assertNotNull(store.get(uri11));
        store.setMaxDocumentCount(1);
        assertNotNull(store.get(uri11));
        assertArrayEquals(store.get(uri12).getDocumentTxt().getBytes(), bytes12);
    }

    @Test
    void setMaxDocsWithUndo(){
        store.delete(uri1);
        assertNull(store.get(uri1));
        store.setMaxDocumentCount(2);
        store.undo();
        store.setMaxDocumentCount(1);
        assertNotNull(store.get(uri1));
        store.delete(uri4);
        store.get(uri2);
        store.delete(uri9);
        store.delete(uri11);
        assertNull(store.get(uri4));
        assertArrayEquals(store.get(uri5).getDocumentTxt().getBytes(), bytes5);
    }

    @Test
    void deletingFromDisk(){
        store.setMaxDocumentCount(3);
        Document two = store.get(uri2);
        store.get(uri7);
        assertTrue(two.hashCode() > 0);
        assertTrue(Files.exists(Path.of("example.com/over/there/name/ferret/u5.json")));
        assertFalse(Files.exists(Path.of("example.com/over/there/name/ferret/u7.json")));
    }

    @Test
    void testPutNewVersionOfDocumentBinary() throws IOException{
        int returned = store.put(new ByteArrayInputStream(this.bytes1), this.uri1, BINARY);
        Document doc1 = store.get(this.uri1);
        assertArrayEquals(this.bytes1, doc1.getDocumentBinaryData(), "failed to return correct binary data");

        int expected = doc1.hashCode();
        returned = store.put(new ByteArrayInputStream(this.bytes2), this.uri1, BINARY);

        assertEquals(expected, returned, "should return hashcode of old document");
        assertArrayEquals(this.bytes2, store.get(this.uri1).getDocumentBinaryData(), "failed to return correct data");
    }
}

