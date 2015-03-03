package net.alexgraham.thesis.supercollider.synths;

import java.util.ArrayList;
import java.util.List;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Effect extends Synth implements Connectable {


	public Effect(Def def) {
		super(def);
		init();
	}
	
	public void init() {
		startCommand = "/effect/add";
		paramChangeCommand = "/effect/paramc";
		closeCommand = "/effect/remove";
		
		addConnector(ConnectorType.AUDIO_INPUT);
		addConnector(ConnectorType.AUDIO_OUTPUT);
	}
	//var effectName = msg[1], effectId = msg[2], toEffectName = msg[4], toEffectId = msg[5];
	
	
	// temporarily override
	public void start() {
		
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	
    	
    	// Add the current parameters
    	for (String paramName : parameterModels.keySet()) {
    		
    		DoubleBoundedRangeModel model = getDoubleModelForParameterName(paramName);
    		
			arguments.add(paramName);
			arguments.add(model.getDoubleValue());
			arguments.add(model.getDoubleMinimum());
			arguments.add(model.getDoubleMaximum());
    	}
    	
    	App.sc.sendMessage(startCommand, arguments.toArray());
	}

	public void connectOutputTo(Effect effect) {
		App.sc.sendMessage("/effect/connect/effect", this.getSynthName(), this.getID(), effect.getSynthName(), effect.getID());
	}
	
	public void disconnectOutput(Effect effect) {
		App.sc.sendMessage("/effect/disconnect/output", this.getSynthName(), this.getID());
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
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				connectOutputTo((Effect) target);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				disconnectOutput((Effect) target);
				return true;
			}
		}
		
		// No connections were made, so return false
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
