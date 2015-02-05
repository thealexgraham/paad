package net.alexgraham.thesis.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.alexgraham.thesis.tests.demos.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.helpers.ComponentMover;
import net.alexgraham.thesis.ui.helpers.ComponentResizer;

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
	}
	
	public JPanel getInterior() {
		return interior;
	}
}
