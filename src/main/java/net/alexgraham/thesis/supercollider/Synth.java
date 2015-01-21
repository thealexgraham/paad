package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.omg.CORBA.INITIALIZE;

import net.alexgraham.thesis.supercollider.SynthDef.Parameter;

public class Synth {
	
	private SynthDef synthDef;
	private SCLang sc;
	private String name;
	private UUID id;
	
	public Synth(SynthDef synthDef, SCLang sc) {
		this.synthDef = synthDef;
		this.sc = sc;
		id = UUID.randomUUID();
	}
	
	public Synth(SynthDef synthDef, SCLang sc, String name) {
		this(synthDef, sc);
		this.name = name;
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
	
	
	/**
	 *  Returns the parameter Name, null if not found
	 * @param name
	 * @return
	 */
	public Parameter getParameterWithName(String name) {
		for (Parameter parameter : synthDef.getParameters()) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		
		return null;
	}
	
	public ArrayList<Parameter> getParameters() {
		return synthDef.getParameters();
	}
	
	public String getSynthName() {
		return synthDef.getSynthName();
	}
	
	public String getID() {
		return id.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}


}
