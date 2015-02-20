package net.alexgraham.thesis.supercollider.synths;

import java.util.UUID;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;

public class Instance {
	
	protected Def def;
	private SCLang sc;
	protected String name;
	protected UUID id;
	
	public Instance(Def def, SCLang sc) {
		// TODO Auto-generated constructor stub
		this.def = def;
		this.sc = sc;
		id = UUID.randomUUID();
	}
	
	
	public String getDefName() {
		return def.getDefName();
	}
	
	public String getID() {
		return id.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return getDefName();
	}
	
	public String toString() {
		return this.name;
	}
}
