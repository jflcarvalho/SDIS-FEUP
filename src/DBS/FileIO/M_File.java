package DBS.FileIO;

import DBS.Chunk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Arrays;

import static DBS.Utils.Utils.getHex;
import static DBS.Utils.Utils.hashString;
import static DBS.Utils.Constants.CHUNKSIZE;

public class M_File {
    private String path;
    private String name;
    private BasicFileAttributes attr;
    private String fileID;
    private File file;
    private byte[] data;
    private Chunk[] chunks;

    public M_File(String path){
        this.path = path;
        init();
    }

    public String getName() {
        return name;
    }

    public String getFileID() {
        return fileID;
    }

    public BasicFileAttributes getAttr() {
        return attr;
    }

    public byte[] getData() {
        return data;
    }

    public File getFile(){
        return file;
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    private void init(){
        file = new File(path);
        if(!file.exists() || file.isDirectory()) {
            file = null;
            return;
        }
        name = file.getName();
        initAttr();
        initHashedName();
        initData();
        initChunks();
    }

    private void initAttr() {
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initHashedName() {
        MessageDigest digest = null;
        String text = name + attr.creationTime().toString() + attr.lastModifiedTime().toString();
        fileID = hashString(text);
    }

    private void initData() {
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initChunks(){
        chunks = new Chunk[data.length / CHUNKSIZE + 1];
        for (int i = 0; i*CHUNKSIZE <= data.length; i++){
            chunks[i] = new Chunk(fileID, Arrays.copyOfRange(data, i, i + CHUNKSIZE > data.length ? data.length : i + CHUNKSIZE));
        }
        Chunk.resetID();
    }

    @Override
    public String toString(){
        if(null == file)
            return "";
        StringBuilder string = new StringBuilder("Path= " + path + "\n");
        string.append("Name= ").append(name).append("\n");
        string.append("FileID= ").append(fileID).append("\n");
        string.append("Data:\n");
        for (int i = 0; i < chunks.length; i++){
            string.append(chunks[i].getChunkID()).append(": ");
            string.append(getHex(chunks[i].getData())).append("\n");
        }
        return string.toString();
    }
}
