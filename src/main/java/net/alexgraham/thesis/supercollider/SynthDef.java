package net.alexgraham.thesis.supercollider;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SynthDef {
	private String synthName;
	private ArrayList<Parameter> parameters;
	
	SCLang sc;
	UUID id;
	
	public SynthDef(String synthName, SCLang sc) {
		this.sc = sc;
		this.synthName = synthName;
		id = UUID.randomUUID();
		parameters = new ArrayList<SynthDef.Parameter>(); // Blank array for params
		
	}
	
	public void start() {
	
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(synthName);
    	arguments.add(id.toString());
    	
    	// Add the current parameters for the synth's default startup
    	for (Parameter param : parameters) {
			arguments.add(param.name);
			arguments.add(param.value);
		}
		sc.sendMessage("/synth/start", arguments.toArray());
	}
	
	public void addParameter(String name, float min, float max, float value) {
		parameters.add(new Parameter(name, min, max, value));
	}
	
	public void changeParameter(String paramName, double value) {
		sc.sendMessage("/synth/paramc", synthName, paramName, id.toString(), value);
	}

	public String getSynthName() {
		return synthName;
	}

	public void setSynthName(String synthName) {
		this.synthName = synthName;
	}
	
	public ArrayList<Parameter> getParameters() {
		return parameters;
	}
	
	public void close() {
		// Stop the synth at ID
    	sc.sendMessage("/synth/stop", synthName, id.toString());
	}
	
	public class Parameter {
		public String name;
		public float min;
		public float max;
		public float value;
		
		public Parameter(String name, float min, float max, float value) {
			this.name = name;
			this.min = min;
			this.max = max;
			this.value = value;
		}
	}
}
