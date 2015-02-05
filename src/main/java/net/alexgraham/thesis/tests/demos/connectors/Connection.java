package net.alexgraham.thesis.tests.demos.connectors;

import java.awt.geom.Line2D;

public class Connection {
	private Connector origin;
	private Connector destination;
	private Line2D line;
	private boolean clicked = false;
	
	public boolean isClicked()  {return clicked; }
	public void setClicked(boolean clicked) { this.clicked = clicked; }
	
	public Connector getOrigin() { return origin; }
	public Connector getDestination() { return destination; }
	
	public Line2D getLine() { return new Line2D.Float(getOrigin().getCurrentCenter(), getDestination().getCurrentCenter()); } 
	
	public Connection(Connector origin, Connector destination) {
		// TODO Auto-generated constructor stub
		this.origin = origin;
		this.destination = destination;
	}
}
