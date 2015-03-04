package net.alexgraham.thesis.supercollider.synths;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Synth extends Instance implements Connectable, java.io.Serializable {
	
	public interface SynthListener {
		public void parameterChanged(String paramName, double value);
		public void synthClosed(Synth synth);
	}
	
	private CopyOnWriteArrayList<SynthListener> synthListeners = 
			new CopyOnWriteArrayList<Synth.SynthListener>();
	
	public Synth(Def def) {
		super(def);
		// Create default values
		init();
	}
	
	public void init() {
		startCommand = "/synth/add";
		paramChangeCommand = "/synth/paramc";
		closeCommand = "/synth/remove";
		addConnector(ConnectorType.AUDIO_OUTPUT);
	}
		
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
	}
		

	
	@Override
	public void start() {
		
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	
    	
    	// Add the current parameters
    	for (String paramName : parameterModels.keySet()) {
			arguments.add(paramName);
			arguments.add(getValueForParameterName(paramName));
		}
    	
    	App.sc.sendMessage(startCommand, arguments.toArray());
	}
	
	
	public void close() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
    	
		// Update Synth Listeners
		for (SynthListener synthListener : synthListeners) {
			synthListener.synthClosed(this);
		}
	}
	
	public String getSynthName() {
		return def.getDefName();
	}

	
	public void connectToEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/synth/connect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}
	
	public void disconnectEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/synth/disconnect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}
	
	// Connectable
	// -------------


	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				disconnectEffect((Effect) target);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean connect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				connectToEffect((Effect) target);
				return true;
			}
		}
		
		// No connections were made, so return false
		return false;
	}
}
