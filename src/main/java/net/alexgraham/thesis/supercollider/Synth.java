package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.alexgraham.thesis.supercollider.SynthDef.Parameter;

public class Synth {
	
	private SynthDef synthDef;
	private SCLang sc;
	UUID id;
	
	public Synth(SynthDef synthDef, SCLang sc) {
		this.synthDef = synthDef;
		this.sc = sc;
		id = UUID.randomUUID();
	}
	public void start() {
		
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(synthDef.getSynthName());
    	arguments.add(id.toString());
    	
    	// Add the current parameters for the synth's default startup
    	for (Parameter param : synthDef.getParameters()) {
			arguments.add(param.name);
			arguments.add(param.value);
		}
		sc.sendMessage("/synth/start", arguments.toArray());
	}
	
	public void changeParameter(String paramName, double value) {
		sc.sendMessage("/synth/paramc", synthDef.getSynthName(), paramName, id.toString(), value);
	}
	
	public void close() {
		// Stop the synth at ID
    	sc.sendMessage("/synth/stop", synthDef.getSynthName(), id.toString());
	}
	
	public ArrayList<Parameter> getParameters() {
		return synthDef.getParameters();
	}
	public String getSynthName() {
		return synthDef.getSynthName();
	}


}
