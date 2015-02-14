package net.alexgraham.thesis.supercollider.synths;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;



public class Instrument extends Synth implements Connectable {


	public Instrument(SynthDef synthDef, SCLang sc) {
		super(synthDef, sc);
		init();
		this.start();

	}
	
	public Instrument(SynthDef synthDef, SCLang sc, String name) {
		this(synthDef, sc);
		this.name = name;
		init();
		this.start();
	}
	
	public void init() {
		startCommand = "/inst/add";
		paramChangeCommand = "/inst/paramc";
		closeCommand = "/inst/remove";
	}

	public void runInstrumentTest() {
		updateParameter("gain", 0.7);
	}
	
	@Override
	public void changeParameter(String paramName, double value) {
		// TODO Auto-generated method stub
		super.changeParameter(paramName, value);
	}

	@Override
	public boolean connectWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		if (otherConnectable instanceof RoutinePlayer) 
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
