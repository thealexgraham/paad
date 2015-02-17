package net.alexgraham.thesis.supercollider.players;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class RoutinePlayer implements Connectable, Serializable {
	
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
	private UUID id;
	boolean playing = false;
	
	private Instrument instrument = null;
	
	private PlayState state = PlayState.DISABLED;
	
	public RoutinePlayer() {
		this.id = UUID.randomUUID();
		App.sc.sendMessage("/routplayer/add", id.toString());
	}
	
	public void start() {
		App.sc.sendMessage("/routplayer/add", getIDString());
	}
	
	/*
	 * Reset for 
	 */
	public void reset() {
		playing = false;
		state = PlayState.DISABLED;
		firePlayStateChange();
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
		inst.getDoubleModelForParameterName("gain").setDoubleValue(0.3);;
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
	
	public void playOrPause() {
		if (playing) {
			stop();
		} else {
			play();
		}
	}
	
	public void play() {
		if (canPlay() && !playing) {
			App.sc.sendMessage("/routplayer/play", this.id.toString());
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
		return false;
	}
	
	// Older, uglier methods
	@Override
	public boolean connect(Connector thisConnector, Connector targetConnector) {
		return false;
	}
	
	@Override
	public boolean disconnect(Connector thisConnector, Connector targetConnector) {
		return false;
	}
	
	@Override
	public boolean connectWith(Connectable otherConnectable) {
		if (otherConnectable instanceof Instrument) {
			connectInstrument((Instrument) otherConnectable);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeConnectionWith(Connectable otherConnectable) {
		System.out.println("Got a remove connection call");
		
		if (otherConnectable instanceof Instrument) {
			System.out.println("Disconnecting Instrument");
			disconnectInstrument((Instrument) otherConnectable);
		}
		return true;
	}
	
}