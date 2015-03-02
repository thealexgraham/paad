package net.alexgraham.thesis.ui.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import net.alexgraham.thesis.AGHelper;
import net.alexgraham.thesis.ui.components.TriangleShape;
import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;

public class ConnectorUI implements java.io.Serializable {

	private int height = 9;
	private int width = 9;

	private Connectable connectable;

	private Location drawLocation = Location.LEFT;
	private ConnectorType type = ConnectorType.DEFAULT;
	
	private Connector connector;
	
	ConnectablePanel owner;
	
	
	public enum Location {
		TOP, BOTTOM, LEFT, RIGHT
	}
	private boolean hovered = false;

	
	public ConnectorUI(ConnectablePanel owner, Connector connector, Location location) {
		
		this.owner = owner;
		this.connectable = connector.getConnectable();
		this.type = connector.getConnectorType();
		this.connector = connector;
		this.drawLocation = location;
		
		if (AGHelper.allEquals(type, ConnectorType.PARAM_CHANGE_IN)) {
			height = 6; width = 6;
		}
		
	}
	
	public void setDrawLocation(Location drawLocation) {
		this.drawLocation = drawLocation;
	}

	public Connectable getConnectable() {
		return connectable;
	}
	
	public Connector getConnector() {
		return connector;
	}

	public ConnectorType getConnectorType() {
		return type;
	}
	
	public TriangleShape getCurrentTriangle() {
		return new TriangleShape(getCurrentPosition(), getTriangleOrigin(), getRotationFromLocation(), width, height);
	}
	
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
						ownerLocation.y + owner.getSize().height / 2);
				break;
			case LEFT:
				location = new Point(ownerLocation.x,
						ownerLocation.y + owner.getSize().height / 2);
				break;
			case BOTTOM:
				location = new Point(ownerLocation.x + (ownerSize.width / 2), ownerLocation.y + owner.getSize().height);
				break;
			case TOP:
				location = new Point(ownerLocation.x + (ownerSize.width / 2), ownerLocation.y);
				break;
			default:
				break;
		}

		return location;
	}

	public Point getCurrentCenter() {
		Point currentPositon = getCurrentPosition();
		Point location = null;
		
		switch (drawLocation) {
			case RIGHT:
				location = new Point(currentPositon.x + width / 2, currentPositon.y);
				break;
			case LEFT:
				location = new Point(currentPositon.x + width, currentPositon.y);
				break;
			case BOTTOM:
				location = new Point(currentPositon.x, currentPositon.y + height / 2);
				break;
			case TOP:
				location = new Point(currentPositon.x, currentPositon.y - height / 2);
				break;
			default:
				return new Point(currentPositon.x + width / 2, currentPositon.y
						+ height / 2);
		}
		
		TriangleShape triangle = getCurrentTriangle();
		Rectangle boundRect = triangle.getTranslatedBounds();
		location = new Point((int)boundRect.getCenterX(), (int)boundRect.getCenterY());
		
		return location;
	}
	
	public int getRotationFromLocation() {
		int rotation = 0;
		switch (drawLocation) {
			case RIGHT:
				rotation = 0; break;
			case BOTTOM:
				rotation = 90; break;
			case LEFT:
				rotation = 180; break;
			case TOP:
				rotation = 270; break;
			default:
				break;
		}
		
		return rotation;
	}
	
	public int getTriangleOrigin() {
		if (type.ordinal() % 2 == 0) {
			// Evens are input, so use the apex (pointing in)
			return TriangleShape.APEX;
		} else {
			// Odds are output, use the base (points out)
			return TriangleShape.BASE;
		}
	}
	
	public Color getColor() {
		return Connector.getColorForType(type);
	}

	public void drawSelf(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		if (hovered) {
			g2.setStroke(new BasicStroke(2));
		} else {
			g2.setStroke(new BasicStroke(1));
		}
		
		Point currentPosition = getCurrentPosition();

		g2.setColor(Color.BLUE);		
		g2.setColor(getColor());
		
		TriangleShape triangle = new TriangleShape(currentPosition, getTriangleOrigin(), getRotationFromLocation(), width, height);
		triangle.draw(g2);
	
//		g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);	
	}

	boolean checkHover(Point position) {
		Point currentPosition = getCurrentPosition();
		TriangleShape triangle = new TriangleShape(currentPosition, getTriangleOrigin(), getRotationFromLocation(), width, height);

		Rectangle ovalRect = triangle.getTranslatedBounds();
		ovalRect.setSize(new Dimension((int) ovalRect.getWidth() * 2, (int) ovalRect.getHeight()*2));

		if (ovalRect.contains(position)) {
			hovered = true;
		} else {
			hovered = false;
		}

		return hovered;
	}
}
