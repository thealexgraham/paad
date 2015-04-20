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
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class PatternGen extends Instance implements Serializable, Connectable {

	private CopyOnWriteArrayList<SynthListener> synthListeners = 
			new CopyOnWriteArrayList<Synth.SynthListener>();
		
	public PatternGen(Def def) {
		super(def);		
		// Create default values
		init();
	}
	
	public void init() {
		startCommand = "/module/add";
		paramChangeCommand = "/module/paramc";
		closeCommand = "/module/remove";
		
		addConnector(ConnectorType.PATTERN_OUT);
		addConnector(ConnectorType.ACTION_IN);
	}
	
	@Override
	public Object[] getStartArguments() {
		// Create the arguments list for this Synth
    	List<Object> arguments = new ArrayList<Object>();
    	arguments.add(def.getType());
    	arguments.add(def.getDefName());
    	arguments.add(id.toString());
    	    	
    	for (ParamModel model : parameterModels.values()) {
    		arguments.add(model.getName());
    		arguments.add(model.getObjectValue());
    	}
    	
    	return arguments.toArray();
	}
	
		
	public void addSynthListener(SynthListener listener) {
		synthListeners.add(listener);
	}
	
	@Override
	public void start() {
    	App.sc.sendMessage(startCommand, getStartArguments());
	}
	
	@Override
	public void stop() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
	}
	
	@Override
	public void close() {
		// Stop the synth at ID
    	App.sc.sendMessage(closeCommand, def.getDefName(), id.toString());
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

}
