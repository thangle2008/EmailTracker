package thangle.emailtracker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.HashSet;

public class FileObjectStream {
    
    /**
     * Load an object from a file
     * @param file file to load object from
     * @return an object in the file (null if there is an exception)
     */
    public static Object readData(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            
            Object data = in.readObject();
            
            in.close();
            return data;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Write an object to a file.
     * @param file the file to write to
     * @param o an object
     */
    public static void writeData(File file, Object o) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            
            out.writeObject(o);
            
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
