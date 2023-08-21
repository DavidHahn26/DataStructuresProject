package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    GsonBuilder builder = new GsonBuilder();

    JsonSerializer<Document> serializer;

    JsonDeserializer<Document> deserializer;

    Gson gson;

    private File file;

    public DocumentPersistenceManager(File baseDir){
        this.file = baseDir;
        serializer = (document, type, jsonSerializationContext) -> {
            JsonObject element = new JsonObject();
            if(document.getDocumentTxt() != null) {
                element.addProperty("documentText", document.getDocumentTxt());
                element.addProperty("wordMap", gson.toJson(document.getWordMap()));
            }
            if(document.getDocumentBinaryData() != null) {
                element.addProperty("documentBinary", DatatypeConverter.printBase64Binary(document.getDocumentBinaryData()));
            }
            element.addProperty("key", document.getKey().toString());
            return element;
        };
        deserializer = (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Type mapToken = new TypeToken<HashMap<String, Integer>>(){}.getType();
            URI uri = URI.create(jsonObject.get("key").getAsString());
            if(jsonObject.has("documentText")){
                String txt = jsonObject.get("documentText").getAsString();
                Map<String, Integer> map = gson.fromJson(jsonObject.get("wordMap").getAsString(), mapToken);
                return new DocumentImpl(uri, txt, map);
            }else{
                byte[] bytes = DatatypeConverter.parseBase64Binary(jsonObject.get("documentBinary").getAsString());
                return new DocumentImpl(uri, bytes);
            }
        };
        builder.registerTypeAdapter(DocumentImpl.class, serializer);
        builder.registerTypeAdapter(DocumentImpl.class, deserializer);
        gson = builder.setPrettyPrinting().create();
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if(uri == null) throw new IllegalArgumentException();
        Path path = Paths.get(getPathDir(uri));
        File file = new File(getPathStr(uri));
        if(!file.exists()) {
            Files.createDirectories(path);
            file.createNewFile();
        }
        PrintWriter pw = new PrintWriter(file);
        pw.println(gson.toJson(val));
        pw.close();
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        //runtime exception if deserialization fails
        if(uri == null) throw new IllegalArgumentException();
        String contents = Files.readString(Path.of(getPathStr(uri)));
        //return getDeserialized(String.valueOf(contents));
        return gson.fromJson(contents, DocumentImpl.class);
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String path = getPathStr(uri);
        boolean toReturn = Files.deleteIfExists(Paths.get(path));
        String[] paths = path.split("/");
        for(int j=1;j<paths.length;j++) {
            String pa = "";
            for (int i=0; i<paths.length-j+1; i++) {
                pa += paths[i] + "/";
            }
            File file = new File(getPathDir(URI.create(pa)));
            if(file.isDirectory() && file.listFiles() != null && file.listFiles().length == 0){
                Files.delete(Path.of(getPathDir(URI.create(pa))));
            }
        }
        return toReturn;
    }

    private String getPathStr(URI uri){ //gets path, including file name
        String full = "";
        if(this.file != null){
            full = this.file.toString();
            if(getCleanedPath(uri).charAt(0) != '/'){
                return full + "/" + getCleanedPath(uri) + ".json";
            }
            return full + getCleanedPath(uri) + ".json";
        }else{
            if(getCleanedPath(uri).charAt(0) == '/'){
                return getCleanedPath(uri).substring(1) + ".json";
            }
            return getCleanedPath(uri) + ".json";
        }
    }

    private String getPathDir(URI uri){ //gets only directory
        String[] full = getCleanedPath(uri).split("/");
        String fin = "";
        if(this.file != null){
            fin += this.file + "/";
        }
        for(int i=0;i<full.length-1;i++){
            if(!full[i].equals("")) {
                if (!(i == full.length - 1 && full[i].contains("."))){
                    fin += full[i] + "/";
                }
            }
        }
        return fin;
    }

    private String getCleanedPath(URI uri){
        String path = "";
        if(!uri.isOpaque()){
            if(uri.getAuthority() != null) path += uri.getAuthority();
            path += uri.getPath();
            if(uri.getQuery() != null) path += uri.getQuery();
            if(uri.getFragment() != null) path += uri.getFragment();
        }else{
            String pathStr = uri.toString().split(":")[1];
            if(pathStr.contains("@")){
                String[] splitPath = pathStr.split("@");
                for(int i=1;i<splitPath.length;i++){
                    path += splitPath[i] + "/";
                }
                path += splitPath[0];
            }
        }
        String[] chars = {":", "+", "=", "{", "}", "?", " ", "@", "#", "$", "%", "^", "&", "<", ">", "|"};
        for(String ch : chars){
            path = path.replace(ch, "");
        }
        path = path.replace("//", "/");
        path = path.replace("\\", "/");

        return path;

    }
}