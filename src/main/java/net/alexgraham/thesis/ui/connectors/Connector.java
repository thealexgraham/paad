package net.alexgraham.thesis.ui.connectors;

import java.awt.BasicStroke;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Connector {
	ConnectablePanel owner;
	
	public interface Connectable {
		boolean connectWith(Connectable otherConnectable);
		boolean removeConnectionWith(Connectable otherConnectable);
	}
	
	public enum Location {
		TOP, BOTTOM, LEFT, RIGHT
	}
	
	private int height = 5;
	private int width = 5;
	
	Connectable connectable;
	
	Location drawLocation = Location.LEFT;
	public void setDrawLocation(Location drawLocation) { this.drawLocation = drawLocation; }
	
	boolean hovered = false;
	public Connector(ConnectablePanel connectablePanel, Connectable connectable) {
		// TODO Auto-generated constructor stub
		this.owner = connectablePanel;
		this.connectable = connectable;
	}
	
	public Connectable getConnectable() {
		return connectable;
	}
	
	public Point getCurrentPosition() {
		Point location = null;
		Point ownerLocation = owner.getLocation();
		Dimension ownerSize = owner.getSize();
		
    	Container parent = owner;
    	while (parent.getClass() != LineConnectPanel.class) { 
    		parent = parent.getParent();
    	}
    	ownerLocation = SwingUtilities.convertPoint(owner.getParent(), ownerLocation, parent);
    	
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
				location = new Point(ownerLocation.x + (ownerSize.width / 2) - width, 
						ownerLocation.y + owner.getSize().height);
				break;
			case TOP:
				location = new Point(ownerLocation.x + (ownerSize.width / 2) - width, 
						ownerLocation.y - height * 2);
				break;
			default:
				break;
		}
		
		return location;
	}
	
	public Point getCurrentCenter() {
		Point currentPositon = getCurrentPosition();
		return new Point(currentPositon.x + width / 2, 
				currentPositon.y + height / 2);
	}
	
	public void drawSelf(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (hovered) {
			g2.setStroke(new BasicStroke(2));
		} else {
			g2.setStroke(new BasicStroke(1));
		}
		
		Point currentPosition = getCurrentPosition();
//		System.out.println(currentPosition);
		
		g2.drawOval(currentPosition.x, currentPosition.y, 10, 10);
	}
	
	public boolean checkHover(Point position) {
		Point currentPosition = getCurrentPosition();
		Rectangle ovalRect = new Rectangle(currentPosition.x, currentPosition.y, 
				10, 10);
		
		if (ovalRect.contains(position.x, position.y)) {
			hovered = true;
		} else {
			hovered = false;
		}
		
		return hovered;
	}
}
