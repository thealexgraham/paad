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

public class Connector implements java.io.Serializable {
	


	public interface Connectable {
		
		boolean connect(Connection connection);
		boolean disconnect(Connection connection);
		
		boolean disconnect(Connector thisConnector, Connector targetConnector);
		boolean connect(Connector thisConnector, Connector targetConnector);
		
		public boolean connectWith(Connectable otherConnectable);
		public boolean removeConnectionWith(Connectable otherConnectable);
	}

	private int height = 10;
	private int width = 10;

	private Connectable connectable;

	private Location drawLocation = Location.LEFT;
	private ConnectorType type = ConnectorType.DEFAULT;
	
	ConnectablePanel owner;
	
	
	public enum Location {
		TOP, BOTTOM, LEFT, RIGHT
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
	
	
	public void setDrawLocation(Location drawLocation) {
		this.drawLocation = drawLocation;
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
				location = new Point(ownerLocation.x - (width),
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
		
		g2.setColor(getColor());
		
//		if (type == ConnectorType.AUDIO_INPUT || type == ConnectorType.AUDIO_OUTPUT) {
//			g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);
//			drawTriangle(g2, currentPosition, width, height * triangleDirection);
//		} else {
////			g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);
//		}
		
		TriangleShape triangle = new TriangleShape(currentPosition, getTriangleOrigin(), getRotationFromLocation(), width, height);
		triangle.draw(g2);
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
		
	public void drawTriangleFromBase(Graphics2D g2, Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x, y + baseLength / 2);
		Point base2 = new Point(x, y - baseLength / 2);
		Point apex = new Point (x + length, y);
		
		drawTriangle(g2, base1, base2, apex);
	}
	
	public void drawTriangleFromApex(Graphics2D g2, Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x + length, y + baseLength / 2);
		Point base2 = new Point(x + length, y - baseLength / 2);
		Point apex = new Point (x, y);
		
		drawTriangle(g2, base1, base2, apex);
	}
	
	public void drawVerticalTriangleFromApex(Graphics2D g2, Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x + baseLength / 2, y + length);
		Point base2 = new Point(x + baseLength / 2, y + length);
		Point apex = new Point (x, y);
		
		drawTriangle(g2, base1, base2, apex);
	}
	
	
	public void drawVerticalTriangleFromBase(Graphics2D g2, Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x + baseLength / 2, y);
		Point base2 = new Point(x + baseLength / 2, y);
		Point apex = new Point (x + length, y);
		
		drawTriangle(g2, base1, base2, apex);
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
