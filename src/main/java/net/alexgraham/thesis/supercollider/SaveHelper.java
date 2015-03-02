package net.alexgraham.thesis.supercollider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveHelper {
	
	private String directory = System.getProperty("user.home") + "\\test\\";
	public SaveHelper() {
		
	}
	
	public void writeObject(String filename, Object object) throws IOException {
		filename = filename + "." + object.getClass().getSimpleName();
    	File outFile = new File(directory + filename);
    	
    	FileOutputStream outStream = new 
    		FileOutputStream(outFile);

    	// Write object with ObjectOutputStream
    	ObjectOutputStream objectStream = new
    		ObjectOutputStream (outStream);

    	// Write object out to disk
    	objectStream.writeObject ( object );
    	objectStream.close();
    	outStream.close();
	}
	
	public Object readObject(String filename, Object toLoad) 
			throws ClassNotFoundException, IOException {
    	filename = filename + "." + toLoad.getClass().getSimpleName();
    	File inFile = new File(directory + filename);
		
        FileInputStream inStream = new FileInputStream(inFile);
        ObjectInputStream objectIn = new ObjectInputStream(inStream);
        Object o = objectIn.readObject();
        objectIn.close();
        inStream.close();
        
        return o;
	}
}
