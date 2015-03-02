package net.alexgraham.thesis.ui.connectors;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;

public class ConnectablePanel extends JPanel {
	

	private ConnectorUI connectorUI;
	
	private boolean pointHover = false;
	private String labelText = null;
	
	public ConnectablePanel() {
		// TODO Auto-generated constructor stub
		super();
	}
	public ConnectablePanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public ConnectablePanel(Location location, Connector connector) {
		connectorUI = new ConnectorUI(this, connector, location);
		connector.addConnectorUI(connectorUI); //TODO: Make sure we delete
	}

	
	public void addConnector(Location location, Connector connector) {
		connectorUI = new ConnectorUI(this, connector, location);
		connector.addConnectorUI(connectorUI);
	}

	public boolean checkPointHover(MouseEvent e) {
		pointHover = connectorUI.checkHover(e.getPoint());
		return pointHover;
	}
	
	public ConnectorUI getConnectorUI() {
		return connectorUI;
	}
	
	public Connector getConnector() {
		return connectorUI.getConnector();
	}

	public Point getConnectionLocation() {
		return connectorUI.getCurrentCenter();
		//return new Point(xPos + width + 5, yPos + height / 2 + 5);
	}
	
	public void paintConnectors(Graphics g) {
		connectorUI.drawSelf(g);
	}

}