package net.alexgraham.thesis.supercollider.synths;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;



public class Effect extends Synth implements Connectable {


	public Effect(SynthDef synthDef, SCLang sc) {
		super(synthDef, sc);
		init();
		this.start();

	}
	
	public Effect(SynthDef synthDef, SCLang sc, String name) {
		this(synthDef, sc);
		this.name = name;
		init();
		this.start();
	}
	
	public void init() {
		startCommand = "/effect/add";
		paramChangeCommand = "/effect/paramc";
		closeCommand = "/effect/remove";
	}
	
	@Override
	public void changeParameter(String paramName, double value) {
		// TODO Auto-generated method stub
		super.changeParameter(paramName, value);
	}

	@Override
	public boolean connectWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		if (otherConnectable instanceof Synth) 
		{
			return true;
		}
		
		return false;
		
	}

	@Override
	public boolean removeConnectionWith(Connectable otherConnectable) {
		return true;
	}

}
