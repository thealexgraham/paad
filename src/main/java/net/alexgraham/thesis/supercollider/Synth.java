package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.omg.CORBA.INITIALIZE;

import com.sun.corba.se.spi.activation._ActivatorImplBase;

import net.alexgraham.thesis.supercollider.SynthDef.Parameter;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;

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
	private Hashtable<String, BoundedRangeModel> parameterModels;
	
	public Synth(SynthDef synthDef, SCLang sc) {
		this.synthDef = synthDef;
		this.sc = sc;
		id = UUID.randomUUID();
		synthListeners = new ArrayList<Synth.SynthListener>();
		parameters = new Hashtable<String, Double>();
		parameterModels = new Hashtable<String, BoundedRangeModel>();
		
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
		sc.sendMessage("/synth/start", arguments.toArray());
	}
	
	public void changeParameter(String paramName, double value) {
		//if (value != parameters.get(paramName)) {
			sc.sendMessage("/synth/paramc", synthDef.getSynthName(), paramName, id.toString(), value);
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
	
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public BoundedRangeModel getModelForParameterName(String name) {
		System.out.println(parameterModels.get(name).toString());
		return parameterModels.get(name);
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
