package net.alexgraham.thesis.supercollider.synths;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
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
		
		super.changeParameter(paramName, value);
	}

	// Implementations //
	/////////////////////
	
	// Connectable
	// ----------------

	@Override
	public boolean connect(Connection connection) {
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		return false;
	}
	
	// Older, uglier methods
	
	@Override
	public boolean connectWith(Connectable otherConnectable) {
		
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

	@Override
	public boolean connect(Connector thisConnector, Connector targetConnector) {
		return false;
	}

	@Override
	public boolean disconnect(Connector thisConnector, Connector targetConnector) {
		return false;
	}

}
