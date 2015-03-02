package net.alexgraham.thesis.supercollider.synths.defs;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;

public class Def implements java.io.Serializable {
	
	/**
	 * 
	 */
	protected String defName;
	private ArrayList<Parameter> parameters;
	

	public Def(String defName, SCLang sc) {
		this.defName = defName;
		
		parameters = new ArrayList<Parameter>(); // Blank array for params
		
	}
	
	public void addParameter(String name, double min, double max, double value) {
		parameters.add(new Parameter(name, min, max, value));
		
	}

	public String getDefName() {
		return defName;
	}

	public void setDefName(String defName) {
		this.defName = defName;
	}
	
	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public String toString() {
		return this.defName + " (" + this.getClass().getSimpleName() + ")";
	}

}
