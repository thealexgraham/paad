package net.alexgraham.thesis.supercollider.synths.defs;

import net.alexgraham.thesis.supercollider.SCLang;

public class SynthDef extends Def {
	public SynthDef(String defName) {
		super(defName);
	}
	
	public SynthDef(String synthName, SCLang sc) {
		super(synthName, sc);
	}
}
