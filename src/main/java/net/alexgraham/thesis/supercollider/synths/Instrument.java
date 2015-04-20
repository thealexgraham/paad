package net.alexgraham.thesis.supercollider.synths;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;



public class Instrument extends Synth implements Connectable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Instrument(Def def) {
		super(def);
		init();
	}
	
	public void init() {
		startCommand = "/module/add";
		paramChangeCommand = "/module/paramc";
		closeCommand = "/module/remove";
		
		addConnector(ConnectorType.INST_PLAY_IN);
		addConnector(ConnectorType.AUDIO_OUTPUT);
	}

	public void runInstrumentTest() {
		updateParameter("gain", 0.7);
	}
	
	public void connectToEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/inst/connect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}
	
	public void disconnectEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/inst/disconnect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}

	
	
	// Implementations //
	/////////////////////

	
	// Connectable
	// -------------- 
	
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

}
