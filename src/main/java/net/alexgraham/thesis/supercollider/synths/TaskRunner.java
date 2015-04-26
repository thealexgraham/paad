package net.alexgraham.thesis.supercollider.synths;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;







import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class TaskRunner extends Synth implements Connectable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public TaskRunner(Def def) {
		super(def);
		init();
	}
	
	public void init() {
		startCommand = "/module/add";
		paramChangeCommand = "/module/paramc";
		closeCommand = "/module/remove";
		
		addConnector(ConnectorType.ACTION_OUT);
		addConnector(ConnectorType.ACTION_IN, "start");
		addConnector(ConnectorType.ACTION_IN, "stop");
		
		createOSCListeners();
	}
	
	public void createOSCListeners() {
		// Create a listener so the connection knows to flash when an action is sent
		App.sc.createListener("/" + this.getID() + "/action/sent", new OSCListener() {
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				Connector actionOutConnector = getConnector(ConnectorType.ACTION_OUT);
				actionOutConnector.flashConnection();
			}
		});
	}
	

	@Override
	public void refreshModels() {
		super.refreshModels();
		createOSCListeners();
	}

	
	public void connectAction(Instance target, String actionType) {
		App.sc.sendMessage("/module/connect/action", this.getDefName(), this.getID(), target.getDefName(), target.getID(), actionType);
	}
	
	public void disconnectAction(Instance target, String actionType) {
		App.sc.sendMessage("/module/disconnect/action", this.getDefName(), this.getID(), target.getDefName(), target.getID(), actionType);	
	}
	
	public void sendPlay() {
		App.sc.sendMessage("/module/action", this.getDefName(), this.getID(), "start");
		//App.sc.sendMessage("/taskrunner/start", this.getDefName(), this.id.toString());
	}
	
	public String getIDString() {
		return id.toString();
	}

	
	// Implementations //
	/////////////////////
	
	// Connectable
	// ------------------

	@Override
	public boolean connect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		Connector targetConnector = connection.getTargetConnector(this);
		
		if (target instanceof Instance) {
			if (connection.isConnectionType(this, ConnectorType.ACTION_OUT, ConnectorType.ACTION_IN)) {
				connectAction((Instance) target, targetConnector.getActionType());
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		Connector targetConnector = connection.getTargetConnector(this);
		
		if (target instanceof Instance) {
			if (connection.isConnectionType(this, ConnectorType.ACTION_OUT, ConnectorType.ACTION_IN)) {
				System.out.println("Disconnecting Action");
				disconnectAction((Instance) target, targetConnector.getActionType());
				return true;
			}
		}
		
		return false;
	}
}
