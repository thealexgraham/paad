package net.alexgraham.thesis.ui.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.alexgraham.thesis.AGHelper;

public class Connector implements java.io.Serializable {
	
	ConnectablePanel owner;

	public interface Connectable {
		
		boolean connect(Connection connection);
		boolean disconnect(Connection connection);
		
		boolean disconnect(Connector thisConnector, Connector targetConnector);
		boolean connect(Connector thisConnector, Connector targetConnector);
		
		public boolean connectWith(Connectable otherConnectable);
		public boolean removeConnectionWith(Connectable otherConnectable);
	}

	public enum Location {
		TOP, BOTTOM, LEFT, RIGHT
	}

	public enum ConnectorType {
		AUDIO_INPUT, AUDIO_OUTPUT, INST_PLAY_IN, INST_PLAY_OUT, PARAM_CHANGE_IN, PARAM_CHANGE_OUT, ACTION_IN, DEFAULT
	}
	
	
	private int height = 10;
	private int width = 10;

	private Connectable connectable;

	private Location drawLocation = Location.LEFT;
	private ConnectorType type = ConnectorType.DEFAULT;

	public void setDrawLocation(Location drawLocation) {
		this.drawLocation = drawLocation;
	}

	private boolean hovered = false;

	public Connector(ConnectablePanel connectablePanel, Connectable connectable) {
		
		this.owner = connectablePanel;
		this.connectable = connectable;
	}

	public Connector(ConnectablePanel connectablePanel,
			Connectable connectable, ConnectorType type) {
		
		this.owner = connectablePanel;
		this.connectable = connectable;
		this.type = type;
	}

	public Connectable getConnectable() {
		return connectable;
	}

	public ConnectorType getConnectorType() {
		return type;
	}
	
	// FIXME: Top positions are way off
	public Point getCurrentPosition() {
		Point location = null;
		Point ownerLocation = owner.getLocation();
		Dimension ownerSize = owner.getSize();

		Container parent = owner;
		while (parent.getClass() != LineConnectPanel.class) {
			parent = parent.getParent();
		}
		ownerLocation = SwingUtilities.convertPoint(owner.getParent(),
				ownerLocation,
				parent);

		switch (drawLocation) {
			case RIGHT:
				location = new Point(ownerLocation.x + owner.getSize().width,
						ownerLocation.y + owner.getSize().height / 2 - height);
				break;
			case LEFT:
				location = new Point(ownerLocation.x - (width * 2),
						ownerLocation.y + owner.getSize().height / 2 - height);
				break;
			case BOTTOM:
				location = new Point(ownerLocation.x + (ownerSize.width / 2)
						- width, ownerLocation.y + owner.getSize().height);
				break;
			case TOP:
				location = new Point(ownerLocation.x + (ownerSize.width / 2)
						- width, ownerLocation.y - height * 2);
				break;
			default:
				break;
		}

		return location;
	}

	public Point getCurrentCenter() {
		Point currentPositon = getCurrentPosition();
		return new Point(currentPositon.x + width / 2, currentPositon.y
				+ height / 2);
	}

	public void drawSelf(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (hovered) {
			g2.setStroke(new BasicStroke(2));
		} else {
			g2.setStroke(new BasicStroke(1));
		}

		Point currentPosition = getCurrentPosition();
		// System.out.println(currentPosition);
		g2.setColor(Color.BLUE);
		
		int triangleDirection = 1;
		
		if(AGHelper.allEquals(type, ConnectorType.AUDIO_INPUT, ConnectorType.INST_PLAY_IN) 
				&& drawLocation == Location.TOP) {
			triangleDirection = -1;	
		}
		
		if(AGHelper.allEquals(type, ConnectorType.AUDIO_INPUT, ConnectorType.AUDIO_OUTPUT)) {
			g2.setColor(Color.RED);
		}
		
		if (type == ConnectorType.AUDIO_INPUT || type == ConnectorType.AUDIO_OUTPUT) {
			g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);
			drawTriangle(g2, currentPosition, width, height * triangleDirection);
		} else {
			g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);
		}
	}
	
	// FIXME: Triangle is not drawing in correct place
	// FIXME: Put flipping in drawSelf probably (maybe with negative heights or something)
	public void drawTriangle(Graphics2D g2, Point location, int width, int height) {
		//location.y = (int)(location.y + height * 1.8f);
		
		// Regular Triangle
		Point point1 = new Point(location.x - width / 2, location.y - height / 2);
		Point point2 = new Point(location.x + width / 2, location.y - height / 2);
		Point point3 = new Point(location.x, location.y + height / 2);
		
		if (type == ConnectorType.AUDIO_INPUT) {
			// Upside Down
			point1 = new Point(location.x + width / 2, location.y + height / 2);
			point2 = new Point(location.x - width / 2, location.y + height / 2);
			point3 = new Point(location.x, location.y - height / 2);
		}
		
		drawTriangle(g2, point1, point2, point3);
	}
	
	public void drawTriangle(Graphics2D g2, Point point1, Point point2, Point point3) {
		g2.drawLine(point1.x, point1.y, point2.x, point2.y);
		g2.drawLine(point1.x, point1.y, point3.x, point3.y);
		g2.drawLine(point2.x, point2.y, point3.x, point3.y);
	}
	
	

	public boolean checkHover(Point position) {
		Point currentPosition = getCurrentPosition();
		Rectangle ovalRect = new Rectangle(currentPosition.x,
				currentPosition.y, 10, 10);

		if (ovalRect.contains(position.x, position.y)) {
			hovered = true;
		} else {
			hovered = false;
		}

		return hovered;
	}
}
