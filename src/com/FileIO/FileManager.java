package com.FileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileManager {
    private static FileInputStream openFile(String filePath){
        File file = new File(filePath);

        FileInputStream fileInput = null;

        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileInput;
    }

    private static void closeFile(FileInputStream file){
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readFile(String filePath){
        byte[] fileByte = null;
        FileInputStream file = openFile(filePath);
        try {
            file.read(fileByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        closeFile(file);
        return fileByte;
    }
}
