package net.alexgraham.thesis.supercollider.synths.parameters;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public interface ParamModel {
	public Instance getOwner();
	public void setOwner(Instance owner);

	public String getName();
	public void setName(String name);

	public Connector getConnector(ConnectorType type);
	public void addConnector(ConnectorType type);
}
