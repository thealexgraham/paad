package net.alexgraham.thesis.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import net.alexgraham.thesis.tests.demos.connectors.ConnectablePanel;

public class MovablePanel extends JPanel {
	
	private JPanel interior;
	private ArrayList<ConnectablePanel> connectables = 
			new ArrayList<ConnectablePanel>();
	
	public ArrayList<ConnectablePanel> getConnectablePanels() { return connectables; }
	public void addConnectablePanel (ConnectablePanel panel) { connectables.add(panel); }
	
	public MovablePanel (int width, int height) {
		interior = new JPanel();
        setLayout(new GridLayout(1, 1));
        add(interior);
        
        setPreferredSize(new Dimension(width, height));
        //ComponentMover mover = new ComponentMover(this, interior);
        //Set up the content pane.
//        ComponentResizer cr = new ComponentResizer();
//	    cr.setSnapSize(new Dimension(10, 10));
//	    cr.registerComponent(this);
        
        //moveable.revalidate();

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        DragListener listener = new DragListener();
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);
	}
	
	class DragListener extends MouseInputAdapter
	{
	    Point location;
	    MouseEvent pressed;
	 
	    public void mousePressed(MouseEvent e)
	    {
	    	boolean pointHover = checkHover(e);
	    	if (!pointHover) { 
		        pressed = e;

	    	} else {
	    		System.out.println("Dispatch");
	    		redispatch(e);

	    	}
	    }
	    
	    @Override
	    public void mouseReleased(MouseEvent e) {
	    	// TODO Auto-generated method stub
	    	super.mouseReleased(e);
    		redispatch(e);
	    }
	    @Override
	    public void mouseMoved(MouseEvent e) {
	    	// TODO Auto-generated method stub
	    	super.mouseMoved(e);
	    	System.out.println("Redispatch");
	    	redispatch(e);
	    }
	 
	    public void mouseDragged(MouseEvent e)
	    {
	    	boolean pointHover = checkHover(e);
	    	if (!pointHover) {
		        //MoveablePanel component = (MoveablePanel) e.getComponent();
	    		Component component = e.getComponent();
		        location = component.getLocation(location);
		        int x = location.x - pressed.getX() + e.getX();
		        int y = location.y - pressed.getY() + e.getY();
		        component.setLocation(x, y);

		        repaint();
		        getParent().getParent().getParent().repaint();
		        for (ConnectablePanel connectablePanel : connectables) {
		        	connectablePanel.setLocation(x, y);
		       }
	    	} else {
	    		System.out.println("Rerouting");
	    		redispatch(e);
	    	}

	     }
	    
	    public boolean checkHover(MouseEvent e) {
	    	boolean hovered = true;
	    	for (ConnectablePanel connectablePanel : connectables) {
				hovered = hovered && 
						connectablePanel.checkPointHover(e);
//						connectablePanel.checkPointHover(SwingUtilities.convertMouseEvent(
//						e.getComponent(), e, connectablePanel));
			}
	    	return hovered;
	    }
	    
	    public void redispatch(MouseEvent e) {
	    	Container parent = getParent();
	    	while ((parent = parent.getParent()).getClass() != JFrame.class) { 
	    		System.out.println(parent.getClass());
	    		//if (parent.getClass() == LineConnectPanel.class) {
 	        		MouseEvent converted = SwingUtilities.convertMouseEvent(e.getComponent(), e, parent);
	        		parent.dispatchEvent(converted);
	    		//}
	    	}
	    	

	    }
	}
	
	public JPanel getInterior() {
		return interior;
	}
}
