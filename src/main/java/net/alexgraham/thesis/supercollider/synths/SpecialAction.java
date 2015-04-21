package net.alexgraham.thesis.supercollider.synths;

import java.io.Serializable;
import java.util.Date;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class SpecialAction extends Instance implements Connectable, Serializable{
	
	private String action = "";
	 public SpecialAction(Def def) {
		super(def);
		action = def.getDefName();
		name = action; //action.replace("Action", ""); //action.substring(0, 1).toUpperCase() + action.substring(1);
//		name = "On " + name;
		init();
	}
	
	public String getAction() {
		return action;
	}
	
	public void init() {	
		addConnector(ConnectorType.ACTION_OUT);
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
	
	public void doAction() {
		App.sc.sendMessage("/special/action", "special", this.action);
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return action;
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}
	
	public void connectAction(Instance target, String actionType) {
		App.sc.sendMessage("/special/connect/action", "special", this.action, target.getDefName(), target.getID(), actionType);
	}
	
	public void disconnectAction(Instance target, String actionType) {
		App.sc.sendMessage("/special/disconnect/action", "special", this.action, target.getDefName(), target.getID(), actionType);	
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
