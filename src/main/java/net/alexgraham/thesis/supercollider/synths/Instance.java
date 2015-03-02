package net.alexgraham.thesis.supercollider.synths;

import java.awt.Point;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.UUID;

import net.alexgraham.thesis.supercollider.SCLang;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class Instance implements Connectable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Def def;
	protected String name;
	protected UUID id;

	protected Point location = new Point(200, 200);
	
	public Point getLocation() { return location; }
	public void setLocation(Point location) { this.location = location; }
	
	public Instance() {

	}
	
	public Instance(Def def, SCLang sc) {
		// TODO Auto-generated constructor stub
		this.def = def;
		id = UUID.randomUUID();
	}

	EnumMap<ConnectorType, Connector> connectors = new EnumMap<ConnectorType, Connector>(ConnectorType.class);
	public Connector getConnector(ConnectorType type) {
		return connectors.get(type); //TODO: might not be the best way to do this
	}
	
	public void addConnector(ConnectorType type) {
		connectors.put(type, new Connector(this, type));
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
	
	public void start() {
		//TODO: Should this be an interface
	}
	
	public void close() {
	}
	
	public void refresh() {
		
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
