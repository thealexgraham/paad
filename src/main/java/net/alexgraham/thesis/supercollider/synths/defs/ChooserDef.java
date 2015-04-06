package net.alexgraham.thesis.supercollider.synths.defs;

import java.util.ArrayList;

import net.alexgraham.thesis.supercollider.SCLang;

public class ChooserDef extends Def {
	
	private ArrayList<String> choices = new ArrayList<String>();

	public ArrayList<String> getChoices() { return this.choices; }
	
	public ChooserDef(String synthName, SCLang sc) {
		super(synthName, sc);
		// TODO Auto-generated constructor stub
	}
	
	public ChooserDef(String defName) {
		super(defName);
		// TODO Auto-generated constructor stub
	}
	
	public void addChoice (String choice) {
		choices.add(choice);
	}
}
