package net.alexgraham.thesis.ui.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class TriangleShape extends Path2D.Double {
	
	public static final int APEX = 0;
	public static final int BASE = 1;
	
	private Point location = new Point(0,0);
	private int rotation = 0;
	
	private Point apex = null;
	
    public Point getLocation() { return location; }
	public void setLocation(Point location) {
		this.location = location;
	}
	
	
	public int getRotation() { return rotation; }
	public void setRotation(int rotation) { this.rotation = rotation; }

	public TriangleShape(Point location, int origin, int rotation, int baseLength, int length) {
    	this.location = location;
    	this.rotation = rotation;
    	if (origin == APEX) {
    		createTriangleFromApex(new Point(0, 0), baseLength, length);
    	} else {
    		createTriangleFromBase(new Point(0, 0), baseLength, length);
    	}
    	
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(rotation));
////        at.translate(location.x, location.y);
        transform(at);
    	
    	
    }
    
	public void createTriangleFromApex(Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x + length, y + baseLength / 2);
		Point base2 = new Point(x + length, y - baseLength / 2);
		Point apex = new Point (x, y);
		
		this.apex = apex;
		
		createTriangle(base1, base2, apex);
	}
	
	private void createTriangleFromBase(Point location, int baseLength, int length) {
		int x = location.x;
		int y = location.y;
		
		Point base1 = new Point(x, y + baseLength / 2);
		Point base2 = new Point(x, y - baseLength / 2);
		Point apex = new Point (x + length, y);
		
		this.apex = apex;
		
		createTriangle(base1, base2, apex);
	}
	
	private void createTriangle(Point point1, Point point2, Point point3) {
        moveTo(point1.x, point1.y);
        lineTo(point2.x, point2.y);
        lineTo(point3.x, point3.y);
        closePath();
        
        apex.translate(location.x, location.y);
	}
	
	public Point getApex() {
		
		return this.apex;
	}
	
	public Rectangle getTranslatedBounds() {
		Rectangle newRectangle = getBounds();
		
		newRectangle.translate(location.x, location.y);
		return newRectangle;
	}
	
	public void draw(Graphics2D g2) {
		
        Graphics2D g2d = (Graphics2D) g2.create();
        
        g2d.translate(location.x, location.y);
        g2d.draw(this);
        g2d.dispose();
	}
	
	public void fill(Graphics2D g2) {
		
        Graphics2D g2d = (Graphics2D) g2.create();
        
        g2d.translate(location.x, location.y);
        g2d.fill(this);
        g2d.dispose();
	}
}