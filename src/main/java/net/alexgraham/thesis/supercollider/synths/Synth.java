package net.alexgraham.thesis.supercollider.synths;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;

public class Synth {
	
	public interface SynthListener {
		public void parameterChanged(String paramName, double value);
		public void synthClosed(Synth synth);
	}
	
	private SynthDef synthDef;
	private SCLang sc;
	protected String name;
	protected UUID id;
	
	private CopyOnWriteArrayList<SynthListener> synthListeners = 
			new CopyOnWriteArrayList<Synth.SynthListener>();
	
	private Hashtable<String, Double> parameters = 
			new Hashtable<String, Double>();
	
	private Hashtable<String, BoundedRangeModel> parameterModels = 
			new Hashtable<String, BoundedRangeModel>();
	
	protected String startCommand = "/synth/start";
	protected String paramChangeCommand = "/synth/paramc";
	protected String closeCommand = "/synth/stop";
	
	public Synth(SynthDef synthDef, SCLang sc) {
		this.synthDef = synthDef;
		this.sc = sc;
		id = UUID.randomUUID();		
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

			DoubleBoundedRangeModel model = 
					new DoubleBoundedRangeModel(2, param.getMin(), param.getMax(), param.getValue());

			model.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// Update the SuperCollider
					changeParameter(param.getName(), model.getDoubleValue());
				}
			});

			parameterModels.put(param.getName(), model);
			parameters.put(param.getName(), Double.valueOf(param.getValue()));
		}
		sc.sendMessage(startCommand, arguments.toArray());
	}
	
	public void changeParameter(String paramName, double value) {
		//if (value != parameters.get(paramName)) {
		System.out.println("change command: " + paramChangeCommand);
			sc.sendMessage(paramChangeCommand, synthDef.getSynthName(), paramName, id.toString(), value);
		//	parameters.put(paramName, value);		
		//}
	}
	
	public void updateParameter(String paramName, double value) {
		DoubleBoundedRangeModel model = (DoubleBoundedRangeModel) getModelForParameterName(paramName);
		if (value != model.getDoubleValue()) {
			model.setDoubleValue(value);
		}
	}
	
	public void close() {
		// Stop the synth at ID
    	sc.sendMessage(closeCommand, synthDef.getSynthName(), id.toString());
    	
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
	
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public BoundedRangeModel getModelForParameterName(String name) {
		return parameterModels.get(name);
	}
	
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public DoubleBoundedRangeModel getDoubleModelForParameterName(String name) {
		return (DoubleBoundedRangeModel) parameterModels.get(name);
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
	
	public String toString() {
		return this.name;
	}


}
