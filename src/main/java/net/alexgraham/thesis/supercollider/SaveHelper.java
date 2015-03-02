package net.alexgraham.thesis.supercollider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

public class SaveHelper {
	
	private static String directory = System.getProperty("user.home") + "\\superpad\\";
	private static String extension = "spd";
	public SaveHelper() {
		
	}
	
	public static File chooseFile(String buttonText) {
		JFileChooser chooser = new JFileChooser() {
		    @Override
		    public void approveSelection() {
		        File f = getSelectedFile();
		        if(f.exists() && getApproveButtonText().equals("Save")) {
		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
		        super.approveSelection();
		    }
		};
		
	    chooser.setCurrentDirectory(new File(directory));
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "SuperPAD Files", "spd");
	    chooser.setFileFilter(filter);
	    //int returnVal = chooser.showOpenDialog(parent);
	    int returnVal = chooser.showDialog(null, buttonText);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File file = chooser.getSelectedFile();
		    
		    if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase(extension)) {
		        // filename is OK as-is
		    } else {
//		        file = new File(file.toString() + "." + extension);  // append .xml if "foo.jpg.xml" is OK
//		      ALTERNATIVELY: remove the extension (if any) and replace it
		        file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+"."+extension); 
		    }
		    return file;
	    } else {
	    	return null;
	    }

	}
	
	public static void writeObject(File outFile, Object object) {
    	try {
        	FileOutputStream outStream = new FileOutputStream(outFile);
        	ObjectOutputStream objectStream = new ObjectOutputStream (outStream);

        	// Write object out to disk
        	objectStream.writeObject ( object );
        	objectStream.close();
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Object readObject(File inFile) {
		try {
			FileInputStream inStream = new FileInputStream(inFile);
			ObjectInputStream objectIn = new ObjectInputStream(inStream);
			
	        Object o;
			o = objectIn.readObject();
			objectIn.close();
			inStream.close();
			
	        return o;
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        

	}
}
