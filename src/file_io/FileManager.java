package file_io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static utils.Constants.DEBUG;

public abstract class FileManager {
    /**
     * Method to create one file
     *
     * @param file_path file path to create the file
     * @return boolean with value of creation file success
     */
    public static boolean createFile(String file_path){
        try {
            File outFile = new File(file_path);
            outFile.getParentFile().mkdirs();
            if(!outFile.createNewFile())
                return false;
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Creating File: " + file_path);
        }
        return true;
    }

    /**
     * Method to write data of a already created file
     *
     * @param data data to write on the file
     * @param file_path path of file to write, need to be created
     * @return boolean with value of writing file success
     */
    public static boolean writeFile(byte[] data, String file_path) {
        OutputStream output;
        try {
            output = new FileOutputStream(file_path);
            output.write(data, 0, data.length);
            output.close();
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Writing File: " + file_path);
            return false;
        }
        return true;
    }


    /**
     * Method to delete a file
     *
     * @param file_path path of file to delete
     * @return boolean with value of deletion success
     */
    public static boolean deleteFile(String file_path){
        File file = new File(file_path);
        if(file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                if(DEBUG)
                    e.printStackTrace();
                else
                    System.out.println("[ERROR] Deleting File: " + file_path);
                return false;
            }
        }
        return true;
    }

    /**
     * Method to read data of existing file
     *
     * @param file_path path of file to read
     * @return byte[] of file data
     */
    public static byte[] readFile(String file_path){
        File file = new File(file_path);
        if(file.exists()) {
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                if(DEBUG)
                    e.printStackTrace();
                else
                    System.out.println("[ERROR] Reading File: " + file_path);
            }
        }
        return null;
    }
}
