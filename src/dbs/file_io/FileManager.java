package dbs.file_io;

import dbs.Chunk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileManager {
    public static boolean createFile(String file_path){
        try {
            File outFile = new File(file_path);
            outFile.getParentFile().mkdirs();
            if(!outFile.createNewFile())
                return false;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean writeFile(Chunk chunk, String file_path) {
        OutputStream output;
        try {
            output = new FileOutputStream(file_path);
            output.write(chunk.getData(), 0, chunk.getData().length);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
