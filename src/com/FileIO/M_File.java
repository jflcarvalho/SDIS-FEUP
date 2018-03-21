package com.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

import static com.Utils.Utils.getHex;
import static com.Utils.Utils.hashString;

public class M_File {
    private String path;
    private String name;
    private BasicFileAttributes attr;
    private String fileID;
    private File file;
    private byte[] data;
    private byte[][] chuncks;

    public M_File(String path) {
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

    private void init(){
        file = new File(path);
        name = file.getName();
        initAttr();
        initHashedName();
        initData();
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

    public String toString(){
        String string = "Path= " + path + "\n";
        string += "Name= " + name + "\n";
        string += "FileID= " + fileID + "\n";
        string += "Data:\n" + getHex(data) + "\n";
        return string;
    }
}
