package net.alexgraham.thesis.supercollider;

import java.util.UUID;

import net.alexgraham.thesis.App;

public class RoutinePlayer {
	protected String name;
	private UUID id;
	boolean playing = false;
	
	private Instrument instrument = null;
	
	public RoutinePlayer() {
		this.id = UUID.randomUUID();
		App.sc.sendMessage("/routplayer/add", id.toString());
	}
	
	public void connectInstrument(Instrument inst) {
		App.sc.sendMessage("/routplayer/connect/inst", this.id.toString(), inst.getSynthName(), inst.getID(), 1);
		this.instrument = inst;
	}
	
	public void play() {
		if (!playing && instrument != null) {
			App.sc.sendMessage("/routplayer/play", this.id.toString());
		}
	}
	
	public void stop() {
		if (playing) {
			App.sc.sendMessage("/routplayer/stop", this.id.toString());
		}
	}
	
	public String getIDString() {
		return id.toString();
	}
}
