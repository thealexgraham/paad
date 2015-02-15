package net.alexgraham.thesis.ui.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class ConnectableBox extends JComponent {

	class DragListener extends MouseInputAdapter
	{
	    Point location;
	    MouseEvent pressed;
	 
	    public void mousePressed(MouseEvent me)
	    {
	    	System.out.println("Pressed");
	        pressed = me;
	    }
	 
	    public void mouseDragged(MouseEvent me)
	    {
	        ConnectableBox component = (ConnectableBox) me.getComponent();
	        location = component.getLocation(location);
	        int x = location.x - pressed.getX() + me.getX();
	        int y = location.y - pressed.getY() + me.getY();
	        component.setX(x);
	        component.setY(y);
	        repaint();
	        getParent().repaint();
	        //component.setLocation(x, y);
	     }
	}
	
	private Connector connector;
	
	private int xPos = 50;
	private int yPos = 50;
	private int width = 100;
	private int height = 100;

	private boolean pointHover = false;
	
	public ConnectableBox(Location location) {
		setLocation(xPos, yPos);

		DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
		
		connector = new Connector(this);
		connector.setDrawLocation(location);
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	@Override
	public Point getLocation() {
		
		return new Point(xPos, yPos);
	}
	
//	@Override
//	public void setLocation(int x, int y) {
//		
//		super.setLocation(x, y);
//		System.out.println("Setting location");
//	}

	public boolean checkPointHover(MouseEvent e) {

		boolean pointHover = connector.checkHover(e.getPoint());
		return pointHover;
	}


	
	public Connector getConnector() {
		return connector;
	}

	public Point getConnectionLocation() {
		return connector.getCurrentCenter();
		//return new Point(xPos + width + 5, yPos + height / 2 + 5);
	}

	public void setX(int xPos) {
		this.xPos = xPos;
	}

	public int getX() {
		return xPos;
	}

	public void setY(int yPos) {
		this.yPos = yPos;
	}

	public int getY() {
		return yPos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void paintConnectors(Graphics g) {
		connector.drawSelf(g);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.RED);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, height);

		g.setColor(Color.BLUE);
		// g.drawOval(width - 10, height / 2 - 10, 10, 10);
		// Rectangle ovalRect = new Rectangle(0 + width, 0 + height / 2, 10,
		// 10);
		// System.out.println(ovalRect.toString());
		if (pointHover) {
			// g.fillOval(width - 10, height / 2 - 10, 10, 10);
		}

		setLocation(xPos, yPos);
	}
}
