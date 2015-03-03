package net.alexgraham.thesis.supercollider.synths.parameters;

import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class ChoiceParamModel implements Serializable, ParamModel, Connectable {

	private Instance owner;
	private String name;
	
	public String choiceName;
	public Object[] choiceArray;

	public ChoiceParamModel(String choiceName, Object[] choiceArray) {
		this.choiceName = choiceName;
		this.choiceArray = Arrays.copyOf(choiceArray, choiceArray.length);
		addConnector(ConnectorType.CHOICE_CHANGE_IN);
	}
	
	EnumMap<ConnectorType, Connector> connectors = new EnumMap<ConnectorType, Connector>(ConnectorType.class);
	public Connector getConnector(ConnectorType type) {
		return connectors.get(type); //TODO: might not be the best way to do this
	}
	
	public void addConnector(ConnectorType type) {
		connectors.put(type, new Connector(this, type));
	}
	
	public String getChoiceName() {
		return choiceName;
	}
	
	public Instance getOwner() {
		return this.owner;
	}
	public void setOwner(Instance owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean connect(Connection connection) {
		return false;
	}

	@Override
	public boolean disconnect(Connection connection) {
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
