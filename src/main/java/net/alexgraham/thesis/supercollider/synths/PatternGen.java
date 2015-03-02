package net.alexgraham.thesis.supercollider.synths;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.Synth.SynthListener;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.PatternGenDef;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.ParamModel;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;

public class PatternGen extends Instance implements Serializable, Connectable {

	
	private CopyOnWriteArrayList<SynthListener> synthListeners = 
			new CopyOnWriteArrayList<Synth.SynthListener>();
	
	private Hashtable<String, Double> parameters = 
			new Hashtable<String, Double>();
	
	protected Hashtable<String, ParamModel> parameterModels = 
			new Hashtable<String, ParamModel>();
	
	protected String startCommand = "/patterngen/add";
	protected String paramChangeCommand = "/patterngen/paramc";
	protected String closeCommand = "/patterngen/remove";
	
	public PatternGen(Def def, SCLang sc) {
		super(def, sc);		
		// Create default values
		this.start();
		createParamModels();
	}
	
	public PatternGen(Def def, SCLang sc, String name) {
		this(def, sc);
		this.name = name;
	}
		
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
	}
	
	// FIXME : this is horrible
	public void createParamModels() {
		PatternGenDef def = (PatternGenDef) this.def;
		for (Param baseParam : def.getParams()) {
			
			if (baseParam.getClass() == IntParam.class) {
				IntParam param = (IntParam) baseParam;
				IntParamModel model = new IntParamModel(param.getValue(), param.getMin(), param.getMax());
				model.setName(param.getName());
				model.setOwner(this);
				
				model.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						// Update the SuperCollider
						changeParameter(param.getName(), model.getValue());
					}
				});

				parameterModels.put(param.getName(), model);
								
			} else if (baseParam.getClass() == ChoiceParam.class) {
				ChoiceParam param = (ChoiceParam) baseParam;
				ChoiceParamModel model = new ChoiceParamModel(param.getChoiceName(), param.getChoiceArray());
				model.setName(param.getName());
				model.setOwner(this);
				parameterModels.put(param.getName(), model);
				//TODO : Listener
			}

		}
	}
	
	public void start() {
		
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	
    	// Usually we'd add the arguments 
    	
    	App.sc.sendMessage(startCommand, arguments.toArray());
	}
	
	public void changeParameter(String paramName, Object value) {
			App.sc.sendMessage(paramChangeCommand, def.getDefName(), paramName, id.toString(), value);	
	}
	
	public void updateParameter(String paramName, double value) {
		DoubleBoundedRangeModel model = (DoubleBoundedRangeModel) getModelForParameterName(paramName);
		if (value != model.getDoubleValue()) {
			model.setDoubleValue(value);
		}
	}
	
	public void updateParameter(String paramName, int value) {
		SpinnerNumberModel model = (SpinnerNumberModel) getModelForParameterName(paramName);
		if (value != (Integer) model.getValue()) {
			model.setValue(value);
		}
	}
	
	public Def getDef() {
		return def;
	}
	
	public void close() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
    	
//		// Update Synth Listeners
//		for (SynthListener synthListener : synthListeners) {
//			synthListener.synthClosed(this);
//		}
	}
		
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public ParamModel getModelForParameterName(String name) {
		return parameterModels.get(name);
	}
	
	public ArrayList<ParamModel> getParamModels() {
		return new ArrayList<ParamModel>(parameterModels.values());
	}
	
	public String toString() {
		return this.name;
	}

	
	
	@Override
	public boolean connect(Connection connection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
		// TODO Auto-generated method stub
		return false;
	}

	
	// Old
	
	
	@Override
	public boolean disconnect(Connector thisConnector, Connector targetConnector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect(Connector thisConnector, Connector targetConnector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connectWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeConnectionWith(Connectable otherConnectable) {
		// TODO Auto-generated method stub
		return false;
	}
}
