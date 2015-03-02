package net.alexgraham.thesis.supercollider.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import sun.misc.JavaAWTAccess;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.connectors.Connection;


public class ConnectionModel implements Serializable {
	
	public interface ConnectionModelListener {
		public void connectionAdded(Connection connection);
		public void connectionRemoved(Connection player);
	}
	
	transient CopyOnWriteArrayList<ConnectionModelListener> listeners = 
			new CopyOnWriteArrayList<ConnectionModelListener>();
	
	private CopyOnWriteArrayList<Connection> connections
		= new CopyOnWriteArrayList<Connection>();
	
	
	public void setConnections(CopyOnWriteArrayList<Connection> connections) {
		this.connections = connections;
	}
	
	public ConnectionModel() {
		
	}
	
	public ArrayList<Connection> getConnections() {
		ArrayList<Connection> list = new ArrayList<Connection>();
		for (Enumeration<Connection> e = Collections.enumeration(connections); e.hasMoreElements();)  {
			Connection connection = e.nextElement();
			list.add(connection);
		}
		return list;
	}
	
	public CopyOnWriteArrayList<Connection> getCopyConnections() { return connections; }
	
	public void addListener(ConnectionModelListener l) {
		listeners.add(l);
	}
	
	public void fireConnectionAdded(Connection connection) {
		for (ConnectionModelListener listener : listeners) {
			listener.connectionAdded(connection);
		}
	}
	
	public void fireConnectionRemoved(Connection connection) {
		for (ConnectionModelListener listener : listeners) {
			listener.connectionRemoved(connection);
		}
	}
		
	public void addConnection(Connection connection) {
		//connections.addElement(connection);
		connections.add(connection);
		fireConnectionAdded(connection);
	}
}
