package net.alexgraham.thesis.supercollider.synths.defs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;

public class Def implements java.io.Serializable {
	
	/**
	 * 
	 */
	protected String defName;
	private ArrayList<Parameter> parameters;
	private String functionString = "";
	private String type = "";

	public Def(String defName, SCLang sc) {
		this.defName = defName;
		parameters = new ArrayList<Parameter>(); // Blank array for params
	}
	
	public Def(String defName) {
		this.defName = defName;
		parameters = new ArrayList<Parameter>(); // Blank array for params
	}
	
	public Def(String defName, Def copyDef) {
		this.defName = defName;
		this.parameters = new ArrayList<Parameter>(parameters);
		this.functionString = copyDef.functionString;
		this.type = copyDef.type;
	}
	
	public void addParameter(String name, double min, double max, double value) {
		parameters.add(new Parameter(name, min, max, value));
		
	}
	
	public void createFileDef() {
		File fout = new File( System.getProperty("user.home") + "/" + defName + ".scd");
		
		try {
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			bw.write("~java.sendDefinition(");
			bw.write("\\" + this.defName + ", \\" + type + ",");
			bw.newLine();
			
			bw.write("\t" + functionString + ",");
			bw.newLine();
			bw.write("\t[\n");
			for (Parameter parameter : parameters) {
				bw.write(String.format("\t\t[\\%s, %.2f, %.2f, %.2f],", parameter.name, parameter.min, parameter.max, parameter.value));
				bw.newLine();
			}
			bw.write("\t]\n);");
 
			bw.close();
			
			openIde(fout);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openIde(File file) {
		try {
			ProcessBuilder pb = new ProcessBuilder(App.sc.getScIde(), file.getAbsolutePath());
			Process p = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Getters / Setters //
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getFunctionString() { return functionString; }
	public void setFunctionString(String functionString) { this.functionString = functionString; }


	public String getDefName() { return defName; }
	public void setDefName(String defName) { this.defName = defName; }
	
	public ArrayList<Parameter> getParameters() { return parameters; }

	public String toString() {
		return this.defName + " (" + this.getClass().getSimpleName() + ")";
	}

}
