package net.alexgraham.thesis.ui.connectors;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class ConnectablePanel extends JPanel {
	

	private Connector connector;
	
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

	public ConnectablePanel(Location location, Connectable toConnect, ConnectorType type) {
		connector = new Connector(this, toConnect, type);
		connector.setDrawLocation(location);
	}

	
	public void addConnector(Location location, Connectable toConnect, ConnectorType type) {
		connector = new Connector(this, toConnect, type);
		connector.setDrawLocation(location);
	}

	public boolean checkPointHover(MouseEvent e) {
		pointHover = connector.checkHover(e.getPoint());
		return pointHover;
	}
	
	public Connector getConnector() {
		return connector;
	}

	public Point getConnectionLocation() {
		return connector.getCurrentCenter();
		//return new Point(xPos + width + 5, yPos + height / 2 + 5);
	}
	
	public void paintConnectors(Graphics g) {
		connector.drawSelf(g);
	}

}