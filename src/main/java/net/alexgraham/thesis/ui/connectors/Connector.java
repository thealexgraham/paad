package net.alexgraham.thesis.ui.connectors;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import net.alexgraham.thesis.AGHelper;


public class Connector implements java.io.Serializable {

	public interface Connectable {
		
		boolean connect(Connection connection);
		boolean disconnect(Connection connection);
		
		boolean disconnect(Connector thisConnector, Connector targetConnector);
		boolean connect(Connector thisConnector, Connector targetConnector);
		
		public boolean connectWith(Connectable otherConnectable);
		public boolean removeConnectionWith(Connectable otherConnectable);
	}

	private Connectable connectable;
	private ConnectorType type = ConnectorType.DEFAULT;
	
	transient ArrayList<ConnectorUI> connectorUIs = new ArrayList<ConnectorUI>();
	
	public enum ConnectorType {
		AUDIO_INPUT, AUDIO_OUTPUT, 
		INST_PLAY_IN, INST_PLAY_OUT, 
		PARAM_CHANGE_IN, PARAM_CHANGE_OUT, 
		PATTERN_IN, PATTERN_OUT, 
		CHOICE_CHANGE_IN, CHOICE_CHANGE_OUT, 
		ACTION_IN, ACTION_OUT, 
		DEFAULT
	}
	
	public Connector() {
		connectorUIs = new ArrayList<ConnectorUI>();
	}

	public Connector(Connectable connectable, ConnectorType type) {

		this.connectable = connectable;
		this.type = type;
	}

	public Connectable getConnectable() {
		return connectable;
	}

	public ConnectorType getConnectorType() {
		return type;
	}
	
	public void addConnectorUI(ConnectorUI connectorUI) {
		connectorUIs.add(connectorUI);
	}
	
	public ArrayList<ConnectorUI> getConnectorUIs() {
		return connectorUIs;
	}
	
	public ConnectorUI[] findClosestConnectorUI(Connector destination) {
		// Start with the first of each
		ConnectorUI closestOrigin = connectorUIs.get(0);
		ConnectorUI closestDestination = destination.getConnectorUIs().get(0); // Start with the first
		double closestDistance = closestOrigin.getCurrentCenter().distance(closestDestination.getCurrentCenter());
		
		// Go through each combination
		for (ConnectorUI originConnectorUI : connectorUIs) {
			for (ConnectorUI destinationConnectorUI : destination.getConnectorUIs()) {
				double currentDistance = originConnectorUI.getCurrentCenter().distance(destinationConnectorUI.getCurrentCenter());
				
				// If the new distance is closer, save these two connectors
				if (currentDistance < closestDistance) {
					closestDistance = currentDistance;
					closestOrigin = originConnectorUI;
					closestDestination = destinationConnectorUI;
				}
			}
		}
		ConnectorUI[] closest = {closestOrigin, closestDestination};
		
		// Return the two closest
		return closest;
	}
	
	public static Color getColorForType(ConnectorType type) {
		Color color = Color.black;
		
		if(AGHelper.allEquals(type, ConnectorType.AUDIO_INPUT, ConnectorType.AUDIO_OUTPUT)) {
			color = Color.ORANGE;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.CHOICE_CHANGE_IN, ConnectorType.CHOICE_CHANGE_OUT)) {
			color = Color.GREEN;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.PARAM_CHANGE_IN, ConnectorType.PARAM_CHANGE_OUT)) {
			color = Color.BLUE;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.ACTION_IN, ConnectorType.ACTION_OUT)) {
			color = Color.RED;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.PATTERN_IN, ConnectorType.PATTERN_OUT)) {
			color = Color.MAGENTA;
		}
		
		return color;
	}
	
	private void readObject(java.io.ObjectInputStream in)
		    throws IOException, ClassNotFoundException {
		    in.defaultReadObject();
		    connectorUIs = new ArrayList<ConnectorUI>();
		}
}
