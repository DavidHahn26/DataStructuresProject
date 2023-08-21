package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {

    class HeapUri implements Comparable<HeapUri>{
        URI uri;

        HeapUri(URI uri){
            this.uri = uri;
        }

        long getLastUse() {
            return store.get(uri).getLastUseTime();
        }

        URI getUri(){
            return this.uri;
        }

        void setLastUse(long lastUse){
            store.get(this.uri).setLastUseTime(lastUse);
        }

        @Override
        public int compareTo(HeapUri o){
            if(o == null){
                throw new NullPointerException();
            }
            return Long.compare(this.getLastUse(), o.getLastUse());
        }
    }

    private BTreeImpl<URI, DocumentImpl> store;
    private PersistenceManager dpm;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<URI> wordStore;
    private MinHeapImpl<HeapUri> heap;
    private boolean maxBytesOn;
    private boolean maxDocsOn;
    private int maxBytes;
    private int maxDocs;
    private File baseDir;

    public DocumentStoreImpl(){
        this.store = new BTreeImpl<>();
        this.commandStack = new StackImpl<>();
        this.wordStore = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.maxBytesOn = false;
        this.maxDocsOn = false;
        this.maxBytes = 0;
        this.maxDocs = 0;
        this.dpm = new DocumentPersistenceManager(null);
        this.store.setPersistenceManager(dpm);
    }

    public DocumentStoreImpl(File baseDir){
        this.store = new BTreeImpl<>();
        this.commandStack = new StackImpl<>();
        this.wordStore = new TrieImpl<>();
        this.heap = new MinHeapImpl<>();
        this.maxBytesOn = false;
        this.maxDocsOn = false;
        this.maxBytes = 0;
        this.maxDocs = 0;
        this.baseDir = baseDir;
        this.dpm = new DocumentPersistenceManager(baseDir);
        this.store.setPersistenceManager(dpm);
    }

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException              if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(uri == null || format == null) throw new IllegalArgumentException();
        if(input == null) return deleteDocumentFromPut(uri);
        else{
            if(getPrivate(uri) == null){ //we are adding a new document
                commandStack.push(new GenericCommand<>(uri, (undoURI) -> {
                    removeAllWords(getPrivate(undoURI));
                    deleteUriFromHeap(undoURI); //??
                    store.put(undoURI, null);
                    checkLimits();
                    return true;
                }));
            }else{ //changing an existing document
                DocumentImpl old = (DocumentImpl) getPrivate(uri);
                commandStack.push(new GenericCommand<>(uri, (undoURI) -> {
                    store.put(undoURI, old);
                    deleteUriFromHeap(undoURI); //changed
                    updateDocTimeToNT(old);
                    this.heap.insert(new HeapUri(old.getKey()));
                    removeAllWords(getPrivate(undoURI));
                    addAllWords(old);
                    checkLimits();
                    return true;
                }));
            }
            return insertDocument(input, uri, format);
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        return getPrivate(uri);
    }
    
    private Document getPrivate(URI uri){
        Document doc = this.store.get(uri);
        if(doc != null) {
            doc.setLastUseTime(System.nanoTime());
            if (getHeapUri(uri) == null) {
                this.heap.insert(new HeapUri(uri));
            }else{
                this.heap.reHeapify(getHeapUri(uri));
            }
        }
        checkLimits();
        return doc;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        if(getPrivate(uri) != null){
            DocumentImpl doc = (DocumentImpl) getPrivate(uri);
            commandStack.push(new GenericCommand<>(uri, (undoURI)->{
                addAllWords(doc);
                updateDocTimeToNT(doc); //maybe add another one
                store.put(undoURI, doc);
                this.heap.insert(new HeapUri(doc.getKey())); //change to undo last uri?
                checkLimits();
                return true;
            }));
            removeAllWords(doc);
            deleteFromHeap(doc.getKey());
            store.put(uri, null);
            //deleteUriFromHeap(doc.getKey());
            return getPrivate(uri) == null;
        }
        return false;
    }

    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        checkStackException();
        Undoable toUndo = this.commandStack.pop();
        if(toUndo instanceof GenericCommand<?>){
            toUndo.undo();
        }else if(toUndo instanceof CommandSet<?>){
            ((CommandSet<?>) toUndo).undoAll();
        }else{
            toUndo.undo();
        }
        checkLimits();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        checkStackException();
        StackImpl<Undoable> temp = new StackImpl<>();
        boolean found = false;
        while(this.commandStack.peek() != null && !found){
            if(this.commandStack.peek() instanceof GenericCommand<?>){
                if(!((GenericCommand<?>) this.commandStack.peek()).getTarget().equals(uri)){
                    temp.push(this.commandStack.pop());
                }else{found = true;}
            }else if(this.commandStack.peek() instanceof CommandSet<?>){
                if(!((CommandSet<URI>) this.commandStack.peek()).containsTarget(uri)){
                    temp.push(this.commandStack.pop());
                }else{found = true;}
            }
        }
        checkStackException();
        Undoable toUndo = this.commandStack.pop();
        if(toUndo instanceof CommandSet<?>){
            ((CommandSet<URI>) toUndo).undo(uri);
            if(((CommandSet<?>) toUndo).size() >= 1){
                this.commandStack.push(toUndo);
            }
        }else{toUndo.undo();}
        while(temp.size() > 0){
            this.commandStack.push(temp.pop());
        }
        checkLimits();
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        if(keyword == null){throw new IllegalArgumentException();}
        List<URI> uris = this.wordStore.getAllSorted(keyword, (o1, o2) -> getPrivate(o2).wordCount(keyword) - getPrivate(o1).wordCount(keyword));
        long time = System.nanoTime();
        List<Document> docs = new ArrayList<>();
        for(int i=0;i<uris.size();i++){
            Document doc = getPrivate(uris.get(i));
            updateDocTime(doc, time);
            docs.add(i, doc);
        }
        return docs;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        if(keywordPrefix == null){throw new IllegalArgumentException();}
        List<URI> uris = this.wordStore.getAllWithPrefixSorted(keywordPrefix, (o1, o2) -> {
            int o1Count = 0;
            int o2Count = 0;
            for (String str : getPrivate(o1).getWords()){
                if(str.startsWith(keywordPrefix)){
                    o1Count += getPrivate(o1).wordCount(str);
                }
            }
            for(String str : getPrivate(o2).getWords()){
                if(str.startsWith(keywordPrefix)){
                    o2Count += getPrivate(o2).wordCount(str);
                }
            }
            return o2Count - o1Count;
        });
        long time = System.nanoTime();
        List<Document> docs = new ArrayList<>();
        for(int i=0;i<uris.size();i++){
            Document doc = getPrivate(uris.get(i));
            updateDocTime(doc, time);
            docs.add(i, doc);
        }
        return docs;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        Set<URI> toDelete = this.wordStore.deleteAll(keyword);
        CommandSet<URI> forStack = new CommandSet<>();
        for(URI uri : toDelete){
            Document doc = getPrivate(uri);
            removeAllWords(doc);
            forStack.addCommand(new GenericCommand<>(uri, (undoURI)->{
                addAllWords(doc);
                deleteUriFromHeap(undoURI); //
                updateDocTimeToNT(doc); //maybe add another
                store.put(undoURI, (DocumentImpl)doc);
                this.heap.insert(new HeapUri(undoURI));
                checkLimits();
                return true;
            }));
            deleteFromHeap(doc.getKey());
            store.put(uri, null);
        }
        commandStack.push(forStack);
        return toDelete;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<URI> toDelete = this.wordStore.deleteAllWithPrefix(keywordPrefix);
        CommandSet<URI> forStack = new CommandSet<>();
        for(URI uri : toDelete){
            Document doc = getPrivate(uri);
            removeAllWords(doc);
            forStack.addCommand(new GenericCommand<>(uri, (undoURI)->{
                addAllWords(doc);
                deleteUriFromHeap(undoURI); //
                updateDocTimeToNT(doc); //change?
                store.put(undoURI, (DocumentImpl)doc);
                this.heap.insert(new HeapUri(undoURI));
                checkLimits();
                return true;
            }));
            deleteFromHeap(doc.getKey());
            store.put(uri, null);
        }
        commandStack.push(forStack);
        return toDelete;
    }

    /**
     * set maximum number of documents that may be stored
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        this.maxDocsOn = true;
        this.maxDocs = limit;
        while(getTotalDocs() > limit){
            HeapUri d = this.heap.remove();
            boolean maxDocs = this.maxDocsOn;
            this.maxDocsOn = false;
            Document doc = getPrivate(d.getUri());
            this.maxDocsOn = maxDocs;
            deleteFromHeap(d.getUri());
            try {
                this.store.moveToDisk(doc.getKey());
            }catch (Exception e){
                return;
            }
        }
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if(limit < 0){
            throw new IllegalArgumentException();
        }
        this.maxBytesOn = true;
        this.maxBytes = limit;
        while(getTotalBytes() > limit){
            HeapUri d = this.heap.remove();
            boolean maxBytes = this.maxBytesOn;
            this.maxBytesOn = false;
            Document doc = getPrivate(d.getUri());
            this.maxBytesOn = maxBytes;
            deleteFromHeap(d.getUri());
            try {
                this.store.moveToDisk(doc.getKey());
            }catch (Exception e){
                return;
            }
        }
    }

    private int insertDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        DocumentImpl doc = null;
        if (format == DocumentFormat.TXT) {
            byte[] bytes = input.readAllBytes();
            String txt = new String(bytes, StandardCharsets.UTF_8);
            doc = new DocumentImpl(uri, txt, null);
            if(getPrivate(uri) != null && getPrivate(uri).getDocumentTxt() != null){
                removeAllWords(getPrivate(uri));
            }
            addAllWords(doc);
        } else if (format == DocumentFormat.BINARY) {
            byte[] bytes = input.readAllBytes();
            doc = new DocumentImpl(uri, bytes);
        }else{
            throw new IllegalArgumentException();
        }
        deleteUriFromHeap(doc.getKey());
        doc.setLastUseTime(System.nanoTime());
        if(maxBytesOn && ((doc.getDocumentBinaryData() != null && doc.getDocumentBinaryData().length > this.maxBytes) || (doc.getDocumentTxt() != null && doc.getDocumentTxt().getBytes().length > this.maxBytes))) doc.setLastUseTime(0);
        DocumentImpl returned = store.put(uri, doc);
        this.heap.insert(new HeapUri(doc.getKey()));
        checkLimits();
        return returned != null ? Math.abs(returned.hashCode()) : 0;
    }

    private void addAllWords(Document doc){
        for(String word : doc.getWords()){
            wordStore.put(word, doc.getKey());
        }
    }

    private void  removeAllWords(Document doc){
        for(String word : doc.getWords()){
            wordStore.delete(word, doc.getKey());
        }
    }

    private void checkStackException(){
        if(this.commandStack.size() <= 0){
            throw new IllegalStateException();
        }
    }

    private int deleteDocumentFromPut(URI uri){
        DocumentImpl old = (DocumentImpl) getPrivate(uri);
        delete(uri);
        return old != null ? old.hashCode() : 0;
    }

    private boolean deleteFromHeap(URI uri){
        boolean maxBytes = this.maxBytesOn;
        boolean maxDocs = this.maxDocsOn;
        this.maxDocsOn = false;
        this.maxBytesOn = false;
        getPrivate(uri).setLastUseTime(0);
        updateDocTime(getPrivate(uri), 0);
        this.maxBytesOn = maxBytes;
        this.maxDocsOn = maxDocs;
        this.heap.reHeapify(getHeapUri(uri));
        HeapUri d = this.heap.remove();
        if(d.getUri().equals(uri)){
            return true;
        }else{
            this.heap.insert(d);
            //System.out.println("issue");
            return false;
        }
    }

    private void updateDocTime(Document doc, long time){
        doc.setLastUseTime(time);
        //if(getHeapUri(doc.getKey()) != null) getHeapUri(doc.getKey()).setLastUse(time);
        this.heap.reHeapify(getHeapUri(doc.getKey()));
    }

    private int getTotalBytes(){
        boolean go = true;
        StackImpl<HeapUri> temp = new StackImpl<>();
        int bytes = 0;
        while(go){
            try{
                HeapUri hUri = this.heap.remove();
                URI uri = hUri.getUri();
                if(this.store.get(uri).getDocumentBinaryData() != null){
                    bytes += this.store.get(uri).getDocumentBinaryData().length;
                }else if(this.store.get(uri).getDocumentTxt() != null){
                    bytes += this.store.get(uri).getDocumentTxt().getBytes().length;
                }
                temp.push(hUri);
            }catch (NoSuchElementException e){
                go = false;
            }
        }
        while(temp.size()>0){
            this.heap.insert(temp.pop());
        }
        return bytes;
    }

    private int getTotalDocs(){
        boolean go = true;
        StackImpl<HeapUri> temp = new StackImpl<>();
        int docs = 0;
        while(go){
            try{
                HeapUri uri = this.heap.remove();
                docs++;
                temp.push(uri);
            }catch (NoSuchElementException e){
                go = false;
            }
        }
        while(temp.size()>0){
            this.heap.insert(temp.pop());
        }
        return docs;
    }

    private boolean deleteUriFromHeap(URI uri){
        HeapUri current = null;
        StackImpl<HeapUri> temp = new StackImpl<>();
        while(current == null || !current.getUri().equals(uri)){
            try {
                current = this.heap.remove();
            }catch(NoSuchElementException e){
                while(temp.size() > 0){
                    this.heap.insert(temp.pop());
                }
                return false;
            }
            temp.push(current);
        }
        HeapUri toDelete = temp.pop();
        while(temp.size() > 0){
            this.heap.insert(temp.pop());
        }
        return true;
    }

    private HeapUri getHeapUri(URI uri){
        HeapUri current = null;
        StackImpl<HeapUri> temp = new StackImpl<>();
        while(current == null || !current.getUri().equals(uri)){
            try {
                current = this.heap.remove();
            }catch(NoSuchElementException e){
                while(temp.size() > 0){
                    this.heap.insert(temp.pop());
                }
                return null;
            }
            temp.push(current);
        }
        while(temp.size() > 0){
            this.heap.insert(temp.pop());
        }
        return current;
    }

    private void checkLimits(){
        if(this.maxDocsOn)setMaxDocumentCount(this.maxDocs);
        if(this.maxBytesOn)setMaxDocumentBytes(this.maxBytes);
    }

    private void updateDocTimeToNT(Document doc){
        doc.setLastUseTime(System.nanoTime());
        //if(getHeapUri(doc.getKey()) != null) getHeapUri(doc.getKey()).setLastUse(System.nanoTime());
    }
}
