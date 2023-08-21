package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentPersistenceManagerTest {

    @AfterAll
    static void cleanup() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        dpm.delete(URI.create("/school/test/doc"));
        dpm.delete(URI.create("mailto:email@gmail.com"));
        dpm.delete(URI.create("efij/few/blablabla/wut"));
        dpm.delete(URI.create("https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/URI.html#getHost()"));
    }

    @Test
    void FileWrite() throws IOException {
        DocumentPersistenceManager dm = new DocumentPersistenceManager(new File("C://Users/shimm/Documents/"));
        DocumentPersistenceManager dmt = new DocumentPersistenceManager(null);
        dmt.serialize(URI.create("/school/test/doc"), new DocumentImpl(URI.create("school/test/doc.json"), "bloopu smrlld", null));
        dmt.serialize(URI.create("mailto:email@gmail.com"), new DocumentImpl(URI.create("efie"), "test email", null));
        dmt.serialize(URI.create("efij/few/blablabla/wut"), new DocumentImpl(URI.create("wefiweh"), "hello, you ae semlly".getBytes()));
    }

    @Test
    void FileRead() throws IOException {
        DocumentPersistenceManager dm = new DocumentPersistenceManager(new File("C://Users/shimm/Documents/"));
        DocumentPersistenceManager dmt = new DocumentPersistenceManager(null);
        dmt.serialize(URI.create("/school/test/doc"), new DocumentImpl(URI.create("school/test/doc.json"), "bloopu smrlld", null));
        dmt.serialize(URI.create("mailto:email@gmail.com"), new DocumentImpl(URI.create("efie"), "test email", null));
        dmt.serialize(URI.create("efij/few/blablabla/wut"), new DocumentImpl(URI.create("wefiweh"), "hello, you ae semlly".getBytes()));
        dmt.serialize(URI.create("https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/URI.html#getHost()"), new DocumentImpl(URI.create("bla"), "ewiuf".getBytes()));
        Document doc = dmt.deserialize(URI.create("/school/test/doc"));
        Document doc2 = dmt.deserialize(URI.create("mailto:email@gmail.com"));
        Document doc3 = dmt.deserialize(URI.create("efij/few/blablabla/wut"));
        assertArrayEquals(doc3.getDocumentBinaryData(), "hello, you ae semlly".getBytes());
    }

    @Test
    void FileDelete() throws IOException{
        DocumentPersistenceManager dmt = new DocumentPersistenceManager(null);
        dmt.delete(URI.create("/school//test/doc"));
        assertFalse(dmt.delete(URI.create("ufhwe/fih")));
    }
}
