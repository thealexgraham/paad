package net.alexgraham.thesis.supercollider.synths;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class ChangeFunc extends Synth implements Connectable {

	public ChangeFunc(SynthDef synthDef, SCLang sc) {
		super(synthDef, sc);
		// TODO Auto-generated constructor stub
		init();
		this.start();
	}
	
	
	public void init() {
		startCommand = "/changefunc/add";
		paramChangeCommand = "/changefunc/paramc";
		closeCommand = "/changefunc/remove";
	}
	
	public void doAction() {
		App.sc.sendMessage("/changefunc/doaction", this.getSynthName(), this.getID());
	}
	
	public void connectToParameter(DoubleParamModel param) {
		Synth owner = param.getOwner();
		App.sc.sendMessage("/changefunc/connect/param", this.getSynthName(), this.getID(), owner.getSynthName(), owner.getID(), param.getName());
	}
	
	public void disconnectFromParameter(DoubleParamModel param) {
		Synth owner = param.getOwner();
		App.sc.sendMessage("/changefunc/disconnect/param", this.getSynthName(), this.getID(), owner.getSynthName(), owner.getID(), param.getName());
	}
	
	// Connectable
	// -------------- 
	
	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();

		// Instrument output into Effect input, valid, check if connectors are correct
		if (connection.isConnectionType(this, ConnectorType.PARAM_CHANGE_OUT, ConnectorType.PARAM_CHANGE_IN)) {
			if (target.getClass() == DoubleParamModel.class) {
				disconnectFromParameter((DoubleParamModel) target);
				return true;
			}			
		}
		
		return false;
	}

	@Override
	public boolean connect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();

		// Instrument output into Effect input, valid, check if connectors are correct
		if (connection.isConnectionType(this, ConnectorType.PARAM_CHANGE_OUT, ConnectorType.PARAM_CHANGE_IN)) {
			if (target.getClass() == DoubleParamModel.class) {
				connectToParameter((DoubleParamModel) target);
				return true;
			}			
		}
		
		return false;
	}

}
