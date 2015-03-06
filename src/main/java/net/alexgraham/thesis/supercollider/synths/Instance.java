package net.alexgraham.thesis.supercollider.synths;

import java.awt.Point;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;

import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.SimpleID;
import net.alexgraham.thesis.supercollider.Synchronizer;
import net.alexgraham.thesis.supercollider.Synchronizer.DefAction;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModelFactory;
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
	
	// FIXME : Number ones should probably be combined
	public void createParamModels() {
		for (Param baseParam : def.getParams()) {
			
			ParamModel model = ParamModelFactory.createParamModel(baseParam);
			
			model.setOwner(this);
			model.addInstanceListener(this);
			
			parameterModels.put(model.getName(), model);
		}
	}
	
	public void refreshModels() {
		for (ParamModel model : getParamModels()) {
			// Re-add instance change listener
			model.addInstanceListener(this);
		}
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
	
	/**
	 * @return Returns true if refreshed
	 */
	public boolean editDef() {
		// Create a copy of the current def	
		if (!def.getDefName().equals(name)) {
			// If the def already matches the name, don't need to do this
			def = new Def(def.getDefName() + getID(), def); // Make a copy of the def
			App.defModel.addDef(def); // Add it to the running defs model
		
			
		}
		
		// Java shouldn't add the def immediately, it needs to get a listener from defModel to wait for SuperCollider to send it
		// SC would need to send a message when it is done adding everything...should probably change to one big message
		
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
        	
        	
    		// Disconnect all connections (but do not remove them!)
        	for (Connection connection : App.connectionModel.getConnectionsInvolving(this)) {
				connection.disconnectModules();
			}
			
    		Synchronizer syncer = new Synchronizer();
    		
    		syncer.setStartAction(new DefAction() {
				
				@Override
				public void doAction() {
					// Add the new DefFile to SuperCollider
					App.sc.sendCommand("\"" + defFile.getAbsolutePath().replace("\\", "/") + "\"" + ".load.postln");
				}
			});
    		
			// Edit param model mins and maxes / add and remove, keep current values when the Definition has been added to supercollider
    		syncer.addMessageListener(def.getDefName(), App.defModel, new DefAction() {
				
				@Override
				public void doAction() {
					
					// Def was created, so we can update the parameters
			    	for (Param baseParam : def.getParams()) {
		    			// Get a copy and create a blank Hashmap so any removed values don't exist anymore
		    			LinkedHashMap<String, ParamModel> modelMapCopy = new LinkedHashMap<String, ParamModel>(parameterModels);
		    			parameterModels = new LinkedHashMap<String, ParamModel>();
		    			
		    			ParamModel model = modelMapCopy.get(baseParam.getName());
						// Check if the model exists already
		    			if (model != null) {
		    				// Just update the bounds
		    				model.updateBounds(baseParam);
		    			} else {
		    				// Create a new model
		    				model = ParamModelFactory.createParamModel(baseParam);
		    			}
		    			
		    			parameterModels.put(baseParam.getName(), model);
					}
			    	
			    	// This is probably when we'd need to call the module to update it
				}
			});

    		// Syncer should also wait for the Definition to be ready in SuperCollider
			syncer.addOSCListener("/def/ready/" + def.getDefName()); // Should this have a port to know its from SuperCollider?
			
			// Set what the syncer should do when all of the steps have been completed
    		syncer.setFinalAction( new DefAction() {
				
				@Override
				public void doAction() {
					
		        	// Stop the currently running synth 
		        	//(Does this need to wait for the connections to be done??)
		    		close();
					
					// Start up the Instance in Java and supercollider
					start();
					
					
					// This needs to wait for start to finish
					// Reconnect everything
		        	for (Connection connection : App.connectionModel.getConnectionsInvolving(Instance.this)) {
						connection.connectModules();
					}
		        	
		        	// Delete the file
		    		defFile.delete();

				}
    		});
    		
    		// Start the syncer
    		syncer.start();

    		// Delete the file
			// Adjust modules and such
    		return true;
        	
        } else if (n == JOptionPane.NO_OPTION) {
            // Delete the file
        	defFile.delete();
        	return false;
        } else {
            System.err.println("Something went wrong");
        }

        return false;

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
