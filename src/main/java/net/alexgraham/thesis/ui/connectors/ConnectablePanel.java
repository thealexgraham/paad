package net.alexgraham.thesis.ui.connectors;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import net.alexgraham.thesis.ui.connectors.Connector.Connectable;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class ConnectablePanel extends JComponent {
	

	private Connector connector;
	
	private boolean pointHover = false;
	
	public ConnectablePanel(Location location, Connectable toConnect) {
		connector = new Connector(this, toConnect);
		connector.setDrawLocation(location);
		//DragListener listener = new DragListener();
//		addMouseListener(listener);
//		addMouseMotionListener(listener);

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


//class DragListener extends MouseInputAdapter
//{
//    Point location;
//    MouseEvent pressed;
// 
//    public void mousePressed(MouseEvent e)
//    {
//    	System.out.println("Pressed");
//    	if (!pointHover) { 
//	        pressed = e;
//
//    	} else {
//    		System.out.println("Dispatch");
//    		redispatch(e);
//
//    	}
//    }
//    
//    @Override
//    public void mouseReleased(MouseEvent e) {
//    	// TODO Auto-generated method stub
//    	super.mouseReleased(e);
//		redispatch(e);
//    }
//    @Override
//    public void mouseMoved(MouseEvent e) {
//    	// TODO Auto-generated method stub
//    	super.mouseMoved(e);
//    	redispatch(e);
//    }
// 
//    public void mouseDragged(MouseEvent e)
//    {
//    	if (!pointHover) {
//	        ConnectablePanel component = (ConnectablePanel) e.getComponent();
//	        location = component.getLocation(location);
//	        int x = location.x - pressed.getX() + e.getX();
//	        int y = location.y - pressed.getY() + e.getY();
//	        component.setLocation(x, y);
//
//	        repaint();
//	        getParent().getParent().getParent().repaint();
//	        
//    	} else {
//    		System.out.println("Rerouting");
//    		redispatch(e);
//    	}
//
//     }
//    
//    public void redispatch(MouseEvent e) {
//    	Container parent = getParent();
//    	while ((parent = parent.getParent()).getClass() != JFrame.class) { 
//    		System.out.println(parent.getClass());
//    		//if (parent.getClass() == LineConnectPanel.class) {
//	        		MouseEvent converted = SwingUtilities.convertMouseEvent(e.getComponent(), e, parent);
//        		parent.dispatchEvent(converted);
//    		//}
//    	}
//    	
//
//    }
//}
 