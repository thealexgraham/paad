package net.alexgraham.thesis.ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.alexgraham.thesis.ui.helpers.ComponentMover;
import net.alexgraham.thesis.ui.helpers.ComponentResizer;

public class OldMoveablePanel extends JPanel {
	
	private JPanel moveable;
	
	public OldMoveablePanel (int width, int height) {
		moveable = new JPanel();
        moveable.setLayout(new GridLayout(1, 1));
        moveable.add(this);
        
        moveable.setPreferredSize(new Dimension(width, height));
        ComponentMover mover = new ComponentMover(moveable, this);
        //Set up the content pane.
        ComponentResizer cr = new ComponentResizer();
	    cr.setSnapSize(new Dimension(10, 10));
	    cr.registerComponent(moveable);
        
        //moveable.revalidate();

        moveable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	
	public JPanel getMoveable() {
		return moveable;
	}
}
