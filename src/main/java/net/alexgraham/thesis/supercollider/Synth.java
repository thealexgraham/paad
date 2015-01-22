package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.omg.CORBA.INITIALIZE;

import net.alexgraham.thesis.supercollider.SynthDef.Parameter;

public class Synth {
	
	public interface SynthListener {
		public void parameterChanged(String paramName, double value);
		public void synthClosed(Synth synth);
	}
	
	private SynthDef synthDef;
	private SCLang sc;
	private String name;
	private UUID id;
	
	private ArrayList<SynthListener> synthListeners;
	private Hashtable<String, Double> parameters; 
	
	public Synth(SynthDef synthDef, SCLang sc) {
		this.synthDef = synthDef;
		this.sc = sc;
		id = UUID.randomUUID();
		synthListeners = new ArrayList<Synth.SynthListener>();
		parameters = new Hashtable<String, Double>();
	}
	
	public Synth(SynthDef synthDef, SCLang sc, String name) {
		this(synthDef, sc);
		this.name = name;
	}
	
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
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
			
			parameters.put(param.getName(), Double.valueOf(param.getValue()));
		}
		sc.sendMessage("/synth/start", arguments.toArray());
	}
	
	public void changeParameter(String paramName, double value) {
		if (value != parameters.get(paramName)) {
			sc.sendMessage("/synth/paramc", synthDef.getSynthName(), paramName, id.toString(), value);
			parameters.put(paramName, value);
			
			// Update Synth Listeners
			for (SynthListener synthListener : synthListeners) {
				synthListener.parameterChanged(paramName, value);
			}			
		}
	}
	
	public void close() {
		// Stop the synth at ID
    	sc.sendMessage("/synth/stop", synthDef.getSynthName(), id.toString());
    	
		// Update Synth Listeners
		for (SynthListener synthListener : synthListeners) {
			synthListener.synthClosed(this);
		}
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
