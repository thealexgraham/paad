package net.alexgraham.thesis.supercollider;

import java.io.File;
import java.io.IOException;

import net.alexgraham.thesis.App;

public class FileHelper {
	public static Process openIde(File file) {
		try {
			ProcessBuilder pb = new ProcessBuilder(App.sc.getScIde(), file.getAbsolutePath());
			Process p = pb.start();
			
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
	
	public static String getSCCodeDir() {
		String pwd = System.getProperty("user.dir");
		
		return pwd + "/src/main/sc";
	}
	
//	public static String getSC() {
//		String system = System.getProperty("os.name");
//		String pwd = System.getProperty("user.dir");
//		String scFile;
//
//		if (system.equals("Mac OS X")) {
//			scFile = pwd + "/src/main/sc/run.scd";
//			startSCLang("/Applications/SuperCollider.app/Contents/Resources/", "sclang", sendPort, scFile, onRun);
//		} else {
//			scFile = "C:/Users/Alex/Dropbox/Thesis/thesis-code/workspace/agthesis-java/src/main/sc/run.scd";
//			startSCLang("C:/Users/Alex/supercollider/", "sclang.exe", sendPort, scFile, onRun);
//		}
//	}
}
