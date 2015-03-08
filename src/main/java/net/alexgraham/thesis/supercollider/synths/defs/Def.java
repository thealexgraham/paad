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
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;

public class Def implements java.io.Serializable {
	
	/**
	 * 
	 */
	protected String defName;
	private ArrayList<Param> parameters;	
	private String functionString = "";
	private String type = "";

	public Def(String defName, SCLang sc) {
		this.defName = defName;
		parameters = new ArrayList<Param>(); // Blank array for params
	}
	
	public Def(String defName) {
		this.defName = defName;
		parameters = new ArrayList<Param>(); // Blank array for params
	}
	
	public Def(String defName, Def copyDef) {
		this.defName = defName;
		this.parameters = new ArrayList<Param>(copyDef.getParams());
		this.functionString = copyDef.functionString;
		this.type = copyDef.type;
	}
	
	
	/**
	 * Removes all parameters so Def can start fresh
	 */
	public void clearParameters() {
		parameters = new ArrayList<Param>();
	}
	
	public void addParameter(String name, double min, double max, double value) {
		parameters.add(new DoubleParam(name, min, max, value));
	}
	
	public void addParameter(String name, int min, int max, int value) {
		parameters.add(new IntParam(name, min, max, value));
	}
	
	public void addParameter(String name, String choiceName, Object[] choiceArray) {
		parameters.add(new ChoiceParam(name, choiceName, choiceArray));
	}
	
	public ArrayList<Param> getParams() {
		return parameters;
	}

	
	public File createFileDef() {
		File fout = new File( System.getProperty("user.home") + "/" + defName + ".scd");
		
		try {
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			bw.write("~java.addDefinition(");
			bw.write("\\" + this.defName + ", \\" + type + ",");
			bw.newLine();
			
			bw.write("\t" + functionString + ",");
			bw.newLine();
			bw.write("\t[\n");
			for (Param parameter : parameters) {
				if (parameter.getClass() == DoubleParam.class) {
					DoubleParam param = (DoubleParam) parameter;
					bw.write(String.format("\t\t[\\%s, \\float, %.2f, %.2f, %.2f],", param.name, param.min, param.max, param.value));
				} else if (parameter.getClass() == IntParam.class) {
					IntParam param = (IntParam) parameter;
					bw.write(String.format("\t\t[\\%s, \\int, %d, %d, %d],", param.name, param.min, param.max, param.value));
				} else if (parameter.getClass() == ChoiceParam.class) {
					ChoiceParam param = (ChoiceParam) parameter;
					bw.write(String.format("\t\t[\\%s, \\choice, [\"%s\", %s]],", param.name, param.choiceName, choiceArrayToString(param.choiceArray)));
				}
				bw.newLine();
			}
			bw.write("\t]\n);");
 
			bw.close();
			
			return fout;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fout;
	}
	
	public String choiceArrayToString(Object[] choiceArray) {
		String choiceString = "[";
		for (Object object : choiceArray) {
			choiceString = choiceString + object.toString() + ", ";
		}
		choiceString = choiceString.substring(0, choiceString.length() - 2); // Remove trailing ,
		choiceString = choiceString + "]";
		
		return choiceString;
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
	
	public ArrayList<Param> getParameters() { return parameters; }

	public String toString() {
		return this.defName + " (" + this.getClass().getSimpleName() + ")";
	}

}
