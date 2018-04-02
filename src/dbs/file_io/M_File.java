package dbs.file_io;

import dbs.Chunk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import static dbs.utils.Constants.CHUNK_SIZE;
import static dbs.utils.Constants.DEBUG;
import static dbs.utils.Utils.hashString;

public class M_File {
    private final String path;
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

    public static String getFileID(String file_path){
        File file = new File(file_path);
        if(!file.exists() || file.isDirectory()) {
            return null;
        }
        String name = file.getName();
        BasicFileAttributes attr = initAttr(file);
        return initHashedName(name, attr);
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
        attr = initAttr(file);
        fileID = initHashedName(name, attr);
        data = initData(file);
        chunks = initChunks(fileID, data);
    }

    private static BasicFileAttributes initAttr(File file) {
        try {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Reading File Attributes");
        }
        return null;
    }

    private static String initHashedName(String name, BasicFileAttributes attr) {
        String text = name + attr.creationTime().toString() + attr.lastModifiedTime().toString();
        return hashString(text);
    }

    private static byte[] initData(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Reading File Data");
        }
        return null;
    }

    private static Chunk[] initChunks(String fileID, byte[] data){
        Chunk[] chunks = new Chunk[data.length / CHUNK_SIZE + 1];
        for (int i = 0; i* CHUNK_SIZE <= data.length; i++){
            chunks[i] = new Chunk(fileID, Arrays.copyOfRange(data, i* CHUNK_SIZE, (i + 1) * CHUNK_SIZE > data.length ? data.length : (i + 1 ) * CHUNK_SIZE));
        }
        Chunk.resetID();
        return chunks;
    }

    @Override
    public String toString(){
        if(null == file)
            return "";
        String string = "Path= " + path + "\n" + "Name= " + name + "\n" +
                "FileID= " + fileID + "\n" +
                "Number of Chunks: " + chunks.length;
        return string;
    }
}
