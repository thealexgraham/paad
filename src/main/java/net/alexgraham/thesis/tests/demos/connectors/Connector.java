package net.alexgraham.thesis.tests.demos.connectors;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class Connector {
	ConnectablePanel owner;
	
	public enum Location {
		TOP, BOTTOM, LEFT, RIGHT
	}
	
	private int height = 5;
	private int width = 5;
	
	Location drawLocation = Location.LEFT;
	public void setDrawLocation(Location drawLocation) { this.drawLocation = drawLocation; }
	
	boolean hovered = false;
	public Connector(ConnectablePanel connectablePanel) {
		// TODO Auto-generated constructor stub
		this.owner = connectablePanel;	
	}
	
	public Point getCurrentPosition() {
		Point location = null;
		Point ownerLocation = owner.getLocationOnScreen();
//		System.out.println("Owner Location " + ownerLocation);
//		System.out.println("Owner regular location " + owner.getLocation());
//		System.out.println("Owner size " + owner.getSize());
		switch (drawLocation) {
			case RIGHT:
				location = new Point(ownerLocation.x + owner.getSize().width, 
						ownerLocation.y + owner.getSize().height / 2);
				break;
			case LEFT:
				location = new Point(ownerLocation.x - width * 2, 
						ownerLocation.y + owner.getSize().height / 2 - owner.getSize().height);
				break;
			case BOTTOM:
				location = new Point(ownerLocation.x - width * 2, 
						ownerLocation.y + owner.getSize().height / 2);
				break;
			case TOP:
				location = new Point(ownerLocation.x - width * 2, 
						ownerLocation.y + owner.getSize().height / 2);
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
