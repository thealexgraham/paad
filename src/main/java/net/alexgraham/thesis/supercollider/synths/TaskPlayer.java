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

public class TaskPlayer extends Synth implements Connectable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface TaskListener {
		public void playStateChanged(PlayState state);
	}
	
	public enum PlayState {
		READY, PLAYING, DISABLED
	}
	
	private CopyOnWriteArrayList<TaskListener> listeners = 
			new CopyOnWriteArrayList<TaskPlayer.TaskListener>();
	
	protected String name;
//	private UUID id;
	boolean playing = false;
	
	private Instrument instrument = null;
	
	private PlayState state = PlayState.DISABLED;
	
	public TaskPlayer(Def def) {
		super(def);
		init();
	}
	
	public void init() {
		startCommand = "/module/add";
		paramChangeCommand = "/module/paramc";
		closeCommand = "/module/remove";
		
		addConnector(ConnectorType.ACTION_OUT);
		addConnector(ConnectorType.ACTION_IN, "cycle");
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
		
		App.sc.createListener("/" + this.getID() + "/state", new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				System.out.println("Got state message");
    			List<Object> arguments = message.getArguments();
    			final String state = (String)arguments.get(0);
    			
    			switch (state) {
    				case "playing":
    					playing = true;
    					playStateChange();
    					break;
    				case "reset":
    					playing = false;
    					playStateChange();
    					break;
    			}
			}
		});
	}
	
	/*
	 * Reset for 
	 */
	public void reset() {
		playing = false;
		state = PlayState.DISABLED;
		firePlayStateChange();
	}
	@Override
	public void refreshModels() {
		super.refreshModels();
		createOSCListeners();
		reset();
		System.out.println("Refreshing");
	}
	
	public void addListener(TaskListener l) {
		listeners.add(l);
	}

	
	public void connectAction(Instance target, String actionType) {
		App.sc.sendMessage("/module/connect/action", this.getDefName(), this.getID(), target.getDefName(), target.getID(), actionType);
	}
	
	public void disconnectAction(Instance target, String actionType) {
		App.sc.sendMessage("/module/disconnect/action", this.getDefName(), this.getID(), target.getDefName(), target.getID(), actionType);	
	}
	
	public void playOrPause() {
		if (playing) {
			stop();
		} else {
			play();
		}
	}
	
	public void sendPlay() {
		App.sc.sendMessage("/player/play", this.getDefName(), this.id.toString());
	}
	
	public void play() {
		if (canPlay() && !playing) {
			sendPlay();
			playing = true;
			playStateChange();
		}
	}
	
	public void stop() {
		
		if (playing) {
			App.sc.sendMessage("/player/stop", this.getDefName(), this.id.toString());
			playing = false;
			playStateChange();
		}
	}
	
	public void close() {
		if (playing) {
			stop();
		}
	}
	
	public boolean canPlay() {
		return true;
	}
	
	private void firePlayStateChange() {
		for (TaskListener playerListener : listeners) {
			playerListener.playStateChanged(state);
		}
	}
	
	private void playStateChange() {
		if (playing) {
			state = PlayState.PLAYING;
		} else {
			state = PlayState.READY;
		}
		if (!canPlay()) {
			state = PlayState.DISABLED;
		}
		
		firePlayStateChange();
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
