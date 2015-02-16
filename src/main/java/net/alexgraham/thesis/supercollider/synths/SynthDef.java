package net.alexgraham.thesis.supercollider.synths;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.alexgraham.thesis.supercollider.SCLang;

public class SynthDef implements java.io.Serializable {
	
	/**
	 * 
	 */
	private String synthName;
	private ArrayList<Parameter> parameters;
	
	SCLang sc;
	
	public SynthDef(String synthName, SCLang sc) {
		this.sc = sc;
		this.synthName = synthName;
		
		parameters = new ArrayList<Parameter>(); // Blank array for params
		
	}
	
	public void addParameter(String name, double min, double max, double value) {
		parameters.add(new Parameter(name, min, max, value));
		
	}

	public String getSynthName() {
		return synthName;
	}

	public void setSynthName(String synthName) {
		this.synthName = synthName;
	}
	
	public ArrayList<Parameter> getParameters() {
		return parameters;
	}
	
	public String toString() {
		return this.synthName + " (" + this.getClass().getSimpleName() + ")";
	}

}
