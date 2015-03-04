package net.alexgraham.thesis.supercollider.synths;

import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import javax.swing.BoundedRangeModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SimpleID;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.PatternGenDef;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParam;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.ParamModel;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public abstract class Instance implements Connectable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Def def;
	protected String name;
	protected SimpleID id;
	
	protected LinkedHashMap<String, ParamModel> parameterModels = 
			new LinkedHashMap<String, ParamModel>();

	protected String startCommand = "/instance/add";
	protected String paramChangeCommand = "/instance/paramc";
	protected String closeCommand = "/instance/remove";
	
	protected Point location = new Point(200, 200);
	
	public Point getLocation() { return location; }
	public void setLocation(Point location) { this.location = location; }
	
	public Instance() {
		id = new SimpleID();
	}
	
	public Instance(Def def) {
		id = new SimpleID();
		this.name = def.getDefName() + "-" + getID();
		this.def = def;
		createParamModels();
		//id = UUID.randomUUID();
	}
	
	
	// Parameter Models
	// ----------------------- 
	
	// FIXME : this is horrible
	public void createParamModels() {

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
				
			} else if (baseParam.getClass() == DoubleParam.class) {
				DoubleParam param = (DoubleParam)baseParam;
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
			}
			


		}
	}
	
	
//	public void refresh() {
//		for (BoundedRangeModel model : parameterModels.values()) {
//			model.addChangeListener(new ChangeListener() {
//				@Override
//				public void stateChanged(ChangeEvent e) {
//					// Update the SuperCollider
//					changeParameter(((DoubleParamModel)model).getName(), ((DoubleParamModel)model).getDoubleValue());
//				}
//			});
//			
//			((DoubleParamModel)model).setOwner(this);
//		}
//	}
//	
	
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
	
	/**
	 *  Returns the model for the named parameter
	 * @param name
	 * @return
	 */
	public ParamModel getModelForParameterName(String name) {
		return parameterModels.get(name);
	}
	
	public Object getValueForParameterName(String name) {
		ParamModel model = getModelForParameterName(name);
		return model.getObjectValue();
	}
	
	public ArrayList<ParamModel> getParamModels() {
		return new ArrayList<ParamModel>(parameterModels.values());
	}
	
	
	// Editing
	// -------------------
	
	public void editDef() {
		// Create a copy of the current def
		String newDefName = def.getDefName() + "-" + getID();
		
		if (!def.getDefName().equals(name)) {
			// If the def already matches the name, don't need to do this
			def = new Def(def.getDefName() + "-" + getID(), def); // Make a copy of the def
			App.defModel.addDef(def); // Add it to the running defs model
		}
		
		// Create a file for the def
		File defFile = def.createFileDef();
		
		Object[] options = {"Refresh", "Discard"};
        int n = JOptionPane.showOptionDialog(null,
                        "Definition Edited",
                        "Refresh from changse, or discard?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
        if (n == JOptionPane.YES_OPTION) {
        	
			// Add the new definition to SuperCollider
    		App.sc.sendCommand("\"" + defFile.getAbsolutePath() + "\"" + ".load.postln");
			// Wait till it is added ?
			  		
			// Stop the current running synth
			
    		close();
    		
			// Start the new synth
    		
    		start();
			
			// Edit param model mins and maxes / add and remove, keep current values
			
			// Adjust modules and such 
        	
        } else if (n == JOptionPane.NO_OPTION) {
            // Delete the file
        } else {
            System.err.println("Something went wrong");
        }



	}
	

	// Connector Business
	// ---------------------
	EnumMap<ConnectorType, Connector> connectors = new EnumMap<ConnectorType, Connector>(ConnectorType.class);
	public Connector getConnector(ConnectorType type) {
		return connectors.get(type); //TODO: might not be the best way to do this
	}
	
	public void addConnector(ConnectorType type) {
		connectors.put(type, new Connector(this, type));
	}
	
	// Getters / Setters
	// ---------------------------
	public Def getDef() {
		return def;
	}
	
	public String getDefName() {
		return def.getDefName();
	}
	
	public String getID() {
		return id.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return getDefName();
	}
	
	public String toString() {
		return this.name;
	}
	
	// Interface 
	// ----------------
	public abstract void start();
	
	public void close() {
		System.out.println("Bottom level close was called");
	}
	
	public void refresh() {
	}
	
	// Connectable
	// ---------------------
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
