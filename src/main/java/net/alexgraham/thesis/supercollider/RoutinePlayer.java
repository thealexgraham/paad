package net.alexgraham.thesis.supercollider;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import net.alexgraham.thesis.App;

public class RoutinePlayer {
	
	public interface PlayerListener {
		public void instrumentConnected(Instrument inst);
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
	
	public void firePlayStateChange() {
		for (PlayerListener playerListener : listeners) {
			playerListener.playStateChanged(state);
		}
	}
	
	public void playStateChange() {
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
}