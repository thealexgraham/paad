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

	
	public PatternGenDef(String defName, SCLang sc) {
		super(defName, sc);
		this.defName = defName;
	}
	
	public PatternGenDef(String defName) {
		super(defName);
		this.defName = defName;
	}

}