package net.alexgraham.thesis.ui.connectors;

import java.awt.geom.Line2D;

import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Connection implements java.io.Serializable {
	
	private Connector origin;
	private Connector destination;

	private boolean clicked = false;
	
	public boolean isClicked()  {return clicked; }
	public void setClicked(boolean clicked) { this.clicked = clicked; }
	
	public Connector getOrigin() { return origin; }
	public Connector getDestination() { return destination; }
	
	public Connectable getOriginConnectable() { return origin.getConnectable(); }
	public Connectable getDestinationConnectable() { return destination.getConnectable(); }
	
	public Line2D getLine() {
		// Find the closest connectorUIs
		ConnectorUI[] closest = getOrigin().findClosestConnectorUI(getDestination());
		
		if (closest == null) {
			// Currently missing connectors, return a blank line
			return new Line2D.Float();
		}
		// Draw the line between them
		return new Line2D.Float(closest[0].getCurrentCenter(), closest[1].getCurrentCenter()); 
	}
		
	public Connection(Connector origin, Connector destination) {
		
		this.origin = origin;
		this.destination = destination;
	}
	
	/*
	 * Attempts to disconnect this connection's connectables
	 */
	public boolean disconnectModules() {
		if (getOriginConnectable().disconnect(this) &&
				getDestinationConnectable().disconnect(this)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Attempts to connect this connection's connectables
	 */
	public boolean connectModules() {
		// We have a destination and an origin
		if (getOriginConnectable().connect(this) || 
				getDestinationConnectable().connect(this)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Allows a connectable to find the Connector that is its destination
	 */
	public Connector getTargetConnector(Connectable owner) {
		if (getOrigin().getConnectable() == owner) {
			// If the orgin is the connectable, return the destination
			return getDestination();
		} else {
			// Otherwise its the origin
			return getOrigin();
		}
	}
	
	/*
	 * Allows a connectable to find its own connector
	 */
	public Connector getOwnerConnector(Connectable owner) {
		if (getOrigin().getConnectable() == owner) {
			return getOrigin();
		} else {
			return getDestination();
		}
	}
	/*
	 * Makes sure the connection requested type is valid
	 */
	
	public boolean isConnectionType(Connectable ownerConnectable, 
			ConnectorType ownerType, ConnectorType targetType) {
		
		Connector owner = getOwnerConnector(ownerConnectable);
		Connector target = getTargetConnector(ownerConnectable);
		
		if (owner.getConnectorType() == ownerType &&
				target.getConnectorType() == targetType) {
			
			return true;
		}
		
		return false;
	}
}
