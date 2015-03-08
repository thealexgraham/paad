package net.alexgraham.thesis.supercollider.synths.parameters.models;

import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public interface ParamModel {
	public Instance getOwner();
	public void setOwner(Instance owner);

	public String getName();
	public void setName(String name);

	public Connector getConnector(ConnectorType type);
	public void addConnector(ConnectorType type);
	public void removeConnectorUIs();
	
	public Object getObjectValue();
		
	/**
	 * Adds a change listener to this model to update the instance whenever the model is updated
	 * @param instance
	 */
	public void addInstanceListener(Instance instance);
	
	public void updateBounds(Param newParam); //TODO: Not always bounds
}
