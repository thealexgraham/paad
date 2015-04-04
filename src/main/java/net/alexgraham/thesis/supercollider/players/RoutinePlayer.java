package net.alexgraham.thesis.supercollider.players;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;


import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class RoutinePlayer extends Instance implements Connectable, Serializable {
	
	public interface PlayerListener {
		public void instrumentConnected(Instrument inst);
		public void instrumentDisconnected(Instrument inst);
		public void playStateChanged(PlayState state);
	}
	
	public enum PlayState {
		READY, PLAYING, DISABLED
	}
	
	private CopyOnWriteArrayList<PlayerListener> listeners = 
			new CopyOnWriteArrayList<RoutinePlayer.PlayerListener>();
	
	protected String name;
//	private UUID id;
	boolean playing = false;
	
	private Instrument instrument = null;
	
	private PlayState state = PlayState.DISABLED;
	
	public RoutinePlayer() {
		super();
//		this.id = UUID.randomUUID();
//		App.sc.sendMessage("/routplayer/add", id.toString());
		init();
	}
	
	public RoutinePlayer(Def def) {
		super(def);
		init();
	}
	public void start() {
		App.sc.sendMessage("/routplayer/add", getID());
	}
	
	public void init() {
		startCommand = "/routplayer/add";
		paramChangeCommand = "/routplayer/paramc";
		closeCommand = "/routplayer/remove";
		
		addConnector(ConnectorType.ACTION_OUT);
		addConnector(ConnectorType.INST_PLAY_OUT);
		addConnector(ConnectorType.PATTERN_IN);
	}
	
	/*
	 * Reset for 
	 */
	public void reset() {
		playing = false;
		state = PlayState.DISABLED;
		firePlayStateChange();
	}
	
	public void refreshModels() {
		reset();
	}
	
	public void addListener(PlayerListener l) {
		listeners.add(l);
	}
	
	public void connectInstrument(Instrument inst) {
		if (playing) {
			stop();
		}
		
		App.sc.sendMessage("/routplayer/connect/inst", this.id.toString(), inst.getSynthName(), inst.getID(), 1);
		this.instrument = inst;
		
		for (PlayerListener playerListener : listeners) {
			playerListener.instrumentConnected(inst);
		}
		//TODO: Fix me
		((DoubleParamModel)inst.getModelForParameterName("gain")).setDoubleValue(0.3);;
		playStateChange();
		
	}
	
	public void disconnectInstrument(Instrument inst) {
		if (playing) {
			stop();
		}
		
		App.sc.sendMessage("/routplayer/remove/inst", this.id.toString(), 0);
		instrument = null;
	
		for (PlayerListener playerListener : listeners) {
			playerListener.instrumentDisconnected(inst);
		}
		playStateChange();
	}
	
	public void connectPatternObject(PatternGen patternObj) {
		App.sc.sendMessage("/routplayer/connect/pattern", this.getID(), patternObj.getDefName(), patternObj.getID(), 0);
	}
	
	public void disconnectPatternObject(PatternGen patternObj) {
		App.sc.sendMessage("/routplayer/disconnect/pattern", this.getID(), patternObj.getDefName(), patternObj.getID(), 0);	
	}
	
	public void connectPlayAction(Instance target) {
		App.sc.sendMessage("/routplayer/connect/playaction", this.getID(), target.getDefName(), target.getID(), 0);
	}
	
	public void disconnectPlayAction(Instance target) {
		App.sc.sendMessage("/routplayer/disconnect/playaction", this.getID(), target.getDefName(), target.getID(), 0);	
	}
	
	public void playOrPause() {
		if (playing) {
			stop();
		} else {
			play();
		}
	}
	
	public void sendPlay() {
		App.sc.sendMessage("/routplayer/play", this.id.toString());
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
			App.sc.sendMessage("/routplayer/stop", this.id.toString());
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
		if (instrument != null) {
			return true;
		} else {
			return false;
		}
	}
	
	private void firePlayStateChange() {
		for (PlayerListener playerListener : listeners) {
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
		if (target instanceof Instrument) {
			if (connection.isConnectionType(this, ConnectorType.INST_PLAY_OUT, ConnectorType.INST_PLAY_IN)) {
				connectInstrument((Instrument) target);	
				return true;
			}
		}
		
		if (target instanceof PatternGen) {
			if (connection.isConnectionType(this, ConnectorType.PATTERN_IN, ConnectorType.PATTERN_OUT)) {
				connectPatternObject((PatternGen) target);
				return true;
			}
		}
		
		if (target instanceof Instance) {
			if (connection.isConnectionType(this, ConnectorType.ACTION_OUT, ConnectorType.ACTION_IN)) {
				connectPlayAction((Instance) target);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		if (target instanceof Instrument) {
			if (connection.isConnectionType(this, ConnectorType.INST_PLAY_OUT, ConnectorType.INST_PLAY_IN)) {
				disconnectInstrument((Instrument) target);
				return true;
			}
		}
		
		if (target instanceof PatternGen) {
			if (connection.isConnectionType(this, ConnectorType.PATTERN_IN, ConnectorType.PATTERN_OUT)) {
				disconnectPatternObject((PatternGen) target);
				return true;
			}
		}
		
		if (target instanceof Instance) {
			if (connection.isConnectionType(this, ConnectorType.ACTION_OUT, ConnectorType.ACTION_IN)) {
				disconnectPlayAction((Instance) target);
				return true;
			}
		}
		
		return false;
	}
}
