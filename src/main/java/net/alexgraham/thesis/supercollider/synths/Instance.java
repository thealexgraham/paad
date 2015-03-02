package net.alexgraham.thesis.supercollider.synths;

import java.awt.Point;
import java.io.Serializable;
import java.util.UUID;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;

public class Instance implements Serializable {
	
	protected Def def;
	protected String name;
	protected UUID id;

	protected Point location = new Point(200, 200);
	
	public Point getLocation() { return location; }
	public void setLocation(Point location) { this.location = location; }

	public Instance() {

	}
	
	public Instance(Def def, SCLang sc) {
		// TODO Auto-generated constructor stub
		this.def = def;
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
	
	public void start() {
		//TODO: Should this be an interface
	}
	
	public void refresh() {
		
	}
}
