package net.alexgraham.thesis.supercollider.synths.defs;

import java.util.ArrayList;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;

public class PatternGenDef extends Def implements java.io.Serializable {
	
	/**
	 * 
	 */
	private ArrayList<Param> parameters;
	
	public PatternGenDef(String defName, SCLang sc) {
		super(defName, sc);
		this.defName = defName;
		
		parameters = new ArrayList<Param>(); // Blank array for params
	}
	
	public PatternGenDef(String defName) {
		super(defName);
		this.defName = defName;
		
		parameters = new ArrayList<Param>(); // Blank array for params
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


}