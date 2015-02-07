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

import jdk.internal.org.objectweb.asm.util.CheckAnnotationAdapter;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;

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
	
	public MovablePanel () {
		interior = new JPanel();
        setLayout(new GridLayout(1, 1));
        add(interior);
        
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
	    	boolean pointHover = mouseOverride(e);
    		redispatch(e);

	    	if (!pointHover) { 
		        pressed = e;

	    	} else {
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
	    	super.mouseMoved(e);
	    	redispatch(e);
	    }
	 
	    public void mouseDragged(MouseEvent e)
	    {
	    	boolean pointHover = mouseOverride(e);
	    	if (!pointHover) {
		        //MoveablePanel component = (MoveablePanel) e.getComponent();
	    		Component component = e.getComponent();
		        location = component.getLocation(location);
		        int x = location.x - pressed.getX() + e.getX();
		        int y = location.y - pressed.getY() + e.getY();
		        component.setLocation(x, y);

		        repaint();
		        getParent().getParent().getParent().repaint();
		        
	    	} else {
	    		redispatch(e);
	    	}

	     }
	    
	    public boolean mouseOverride(MouseEvent e) {
	    	Component parent = e.getComponent();
	    	while (parent.getClass() != LineConnectPanel.class) { 
	    		parent = parent.getParent();
	    	}
	    	
	    	if (parent.getClass() == LineConnectPanel.class) {
	    		LineConnectPanel lineConnect = (LineConnectPanel) parent;
	    		return lineConnect.isRequiringMouse();
	    	} else {
	    		return false;
	    	}
	    	
	    }
	    
	    public boolean checkHover(MouseEvent e) {
	    	boolean hovered = false;
	    	
	    	Component parent = e.getComponent();
	    	while (parent.getClass() != LineConnectPanel.class) { 
	    		parent = parent.getParent();
	    	}
	    	MouseEvent converted = SwingUtilities.convertMouseEvent(e.getComponent(), e, parent);
	    	
	    	for (ConnectablePanel connectablePanel : connectables) {
				hovered = hovered || 
						connectablePanel.checkPointHover(converted);
//						connectablePanel.checkPointHover(SwingUtilities.convertMouseEvent(
//						e.getComponent(), e, connectablePanel));
			}
	    	return hovered;
	    }
	    
	    public void redispatch(MouseEvent e) {
	    	Component parent = e.getComponent();
	    	while ((parent = parent.getParent()).getClass() != LineConnectPanel.class) { 
//	    		System.out.println(parent.getClass());
	    		//if (parent.getClass() == LineConnectPanel.class) {
 	        		MouseEvent converted = SwingUtilities.convertMouseEvent(e.getComponent(), e, parent);
	        		parent.dispatchEvent(converted);
	    	}
	    	

	    }
	    
	    public LineConnectPanel getLineConnectPanel(MouseEvent e) {
	    	Component parent = e.getComponent();
	    	while (parent.getClass() != LineConnectPanel.class) { 
	    		parent = parent.getParent();
	    	}
	    	
	    	return (LineConnectPanel) parent;
	    }
	}
	
	public JPanel getInterior() {
		return interior;
	}
}
