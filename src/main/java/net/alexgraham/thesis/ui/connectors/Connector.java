package net.alexgraham.thesis.ui.connectors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.App;


public class Connector implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface Connectable {
		
		boolean connect(Connection connection);
		boolean disconnect(Connection connection);
		
	}

	private Connectable connectable;
	private ConnectorType type = ConnectorType.DEFAULT;
	
	transient List<ConnectorUI> connectorUIs = new CopyOnWriteArrayList<ConnectorUI>();
	
	private boolean flashing = false;
	transient Timer flashTimer;
	private int flashTime = 200;
	
	// Optional
	private String actionType = "default";
	public String getActionType() {
		return actionType;
	}
	
	private List<Connection> connections = new CopyOnWriteArrayList<Connection>();
	
	private void readObject(java.io.ObjectInputStream in)
		    throws IOException, ClassNotFoundException {
		    in.defaultReadObject();
		    connectorUIs = new CopyOnWriteArrayList<ConnectorUI>();
		    createFlashTimer(flashTime);
	}
	
	public void addConnection(Connection connection) {
		connections.add(connection);
	}
	public void removeConnection(Connection connection) {
		connections.remove(connection);
	}
	public List<Connection> getConnections() { return connections; }
	
	
	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}
	
	public boolean isFlashing() {
		return flashing;
	}
	
	public void createFlashTimer(int flashLength) {
		
		ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  if(!flashTimer.isRunning())
		    		  flashing = false;
		    	  else
		    		  flashing = false;
		  		refreshUIs();
		      }
		  };
		  
		  flashTimer = new Timer(flashLength, taskPerformer);
	}
	
	public void flashConnection() {
		for (Connection connection : connections) {
			connection.flashTarget(this);
		}
		flash();
	}
	public void flash() {
		flashTimer.stop();
		flashing = true;
		refreshUIs();
		flashTimer.start();
	}
	
	public void refreshUIs() {
		App.mainWindow.repaint();
	}
	
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
		connectorUIs = new CopyOnWriteArrayList<ConnectorUI>();
		init();
	}

	public Connector(Connectable connectable, ConnectorType type) {

		this.connectable = connectable;
		this.type = type;
		init();
	}
	
	public Connector(Connectable connectable, ConnectorType type, String actionType) {

		this.connectable = connectable;
		this.type = type;
		this.actionType = actionType;
		init();
	}
	
	public void init() {
		createFlashTimer(125);
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
	
	public List<ConnectorUI> getConnectorUIs() {
		return connectorUIs;
	}
	
	public void removeConnectorUIs() {
		connectorUIs.clear();
	}
	
	public ConnectorUI[] findClosestConnectorUI(Connector destination) {
		
		if (connectorUIs.isEmpty() || destination.getConnectorUIs().isEmpty()) {
			// If either is missing connector UIs, return null
			return null;
		}
		
//		for (ConnectorUI connectorUI : connectorUIs) {
//			setclo
//		}
		
		// Start with the first of each
		ConnectorUI closestOrigin = connectorUIs.get(0);
		ConnectorUI closestDestination = destination.getConnectorUIs().get(0); // Start with the first
		double closestDistance = closestOrigin.getCurrentCenter().distance(closestDestination.getCurrentCenter());
		
		// Go through each combination
		for (ConnectorUI originConnectorUI : connectorUIs) {
			for (ConnectorUI destinationConnectorUI : destination.getConnectorUIs()) {
				
				originConnectorUI.setClosest(false);
				destinationConnectorUI.setClosest(false);
				
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
	
	public Color getColor() {
		return Connector.getColorForType(type);
		
	}
	
	public static Color getColorForType(ConnectorType type) {
		Color color = Color.black;
		
		if(AGHelper.allEquals(type, ConnectorType.AUDIO_INPUT, ConnectorType.AUDIO_OUTPUT)) {
			color = Color.ORANGE;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.CHOICE_CHANGE_IN, ConnectorType.CHOICE_CHANGE_OUT)) {
			color = Color.MAGENTA;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.PARAM_CHANGE_IN, ConnectorType.PARAM_CHANGE_OUT)) {
			color = Color.cyan;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.ACTION_IN, ConnectorType.ACTION_OUT)) {
			color = Color.RED;
		}
		
		if(AGHelper.allEquals(type, ConnectorType.PATTERN_IN, ConnectorType.PATTERN_OUT)) {
			color = Color.MAGENTA;
		}
		
		if (AGHelper.allEquals(type, ConnectorType.INST_PLAY_IN, ConnectorType.INST_PLAY_OUT)) {
			color = Color.GREEN;
		}
		
		return color;
	}
	
}
