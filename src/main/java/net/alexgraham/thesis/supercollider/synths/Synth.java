package net.alexgraham.thesis.supercollider.synths;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.ParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Synth extends Instance implements Connectable, java.io.Serializable {
	
	public interface SynthListener {
		public void parameterChanged(String paramName, double value);
		public void synthClosed(Synth synth);
	}
	
	private CopyOnWriteArrayList<SynthListener> synthListeners = 
			new CopyOnWriteArrayList<Synth.SynthListener>();
	
	private Hashtable<String, Double> parameters = 
			new Hashtable<String, Double>();
	
	protected Hashtable<String, BoundedRangeModel> parameterModels = 
			new Hashtable<String, BoundedRangeModel>();
	
	protected String startCommand = "/synth/add";
	protected String paramChangeCommand = "/synth/paramc";
	protected String closeCommand = "/synth/remove";
		
	public Synth(Def def, SCLang sc) {
		super(def, sc);
	
		// Create default values
		createParamModels();
	}
	
	public Synth(Def def, SCLang sc, String name) {
		this(def, sc);
		this.name = name;
	}
		
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
	}
	
	public void createParamModels() {
		for (Parameter param : def.getParameters()) {

			DoubleParamModel model = 
					new DoubleParamModel(2, param.getMin(), param.getMax(), param.getValue());
			
			model.setName(param.getName());
			model.setOwner(this);

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
	}
	
	@Override
	public void refresh() {
		for (BoundedRangeModel model : parameterModels.values()) {
			model.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// Update the SuperCollider
					changeParameter(((DoubleParamModel)model).getName(), ((DoubleParamModel)model).getDoubleValue());
				}
			});
		}
	}
	
	@Override
	public void start() {
		
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	
    	
    	// Add the current parameters
    	for (String paramName : parameterModels.keySet()) {
			arguments.add(paramName);
			arguments.add(getValueForParameterName(paramName));
		}
    	
    	App.sc.sendMessage(startCommand, arguments.toArray());
	}
	
	public void changeParameter(String paramName, double value) {
		//if (value != parameters.get(paramName)) {
			App.sc.sendMessage(paramChangeCommand, def.getDefName(), paramName, id.toString(), value);
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
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
    	
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
		for (Parameter parameter : def.getParameters()) {
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
	
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public double getValueForParameterName(String name) {
		DoubleBoundedRangeModel model = getDoubleModelForParameterName(name);
		return model.getDoubleValue();
	}
	
	
	public ArrayList<Parameter> getParameters() {
		return def.getParameters();
	}
	
	public String getSynthName() {
		return def.getDefName();
	}
	
	public String toString() {
		return this.name;
	}
	
	
	public void connectToEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/synth/connect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}
	
	public void disconnectEffect(Effect effect) {
		//var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
		App.sc.sendMessage("/synth/disconnect/effect", this.getSynthName(), this.id.toString(), effect.getSynthName(), effect.getID());
	}
	
	// Connectable
	// -------------


	@Override
	public boolean disconnect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				disconnectEffect((Effect) target);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean connect(Connection connection) {
		Connectable target = connection.getTargetConnector(this).getConnectable();
		
		if (target.getClass() == Effect.class) {
			// Instrument output into Effect input, valid, check if connectors are correct
			if (connection.isConnectionType(this, ConnectorType.AUDIO_OUTPUT, ConnectorType.AUDIO_INPUT)) {
				// Should return the effect we want, so connect to it
				connectToEffect((Effect) target);
				return true;
			}
		}
		
		// No connections were made, so return false
		return false;
	}
	
	@Override
	public boolean connect(Connector thisConnector, Connector targetConnector) {
		// 
		return false;
	}
	@Override
	public boolean disconnect(Connector thisConnector, Connector targetConnector) {
		return false;
	}

	@Override
	public boolean connectWith(Connectable otherConnectable) {
		return false;
	}

	@Override
	public boolean removeConnectionWith(Connectable otherConnectable) {
		return false;
	}


}
