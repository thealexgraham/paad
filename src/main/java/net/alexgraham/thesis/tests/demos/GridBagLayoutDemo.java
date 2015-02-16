package net.alexgraham.thesis.tests.demos;


import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.ui.components.FlashButton;
import net.alexgraham.thesis.ui.helpers.ComponentMover;
import net.alexgraham.thesis.ui.helpers.ComponentResizer;

public class GridBagLayoutDemo {
    final static boolean shouldFill = true;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    

    public static void addComponentsToPane(Container pane) {
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

    JButton button;
	pane.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	if (shouldFill) {
	//natural height, maximum width
	c.fill = GridBagConstraints.HORIZONTAL;
	}
	
	JLabel titleLabel = new JLabel("title");
	titleLabel.setBackground(Color.LIGHT_GRAY);
	titleLabel.setOpaque(true);
	titleLabel.setAlignmentX(0.5f);
	
	c.fill = GridBagConstraints.BOTH;
	c.gridwidth = 3;
	c.weightx = 1;
	c.gridx = 0;
	c.gridy = 0;
	pane.add(titleLabel, c);
	
	FlashButton flasher = new FlashButton(60, Color.RED);

	button = new JButton("Button 1");
	if (shouldWeightX) {
	c.weightx = 0.5;
	}
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridwidth = 1;
	c.gridx = 0;
	c.gridy = 1;
	button.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			flasher.flash();
		}
	});	

	pane.add(button, c);

	button = new JButton("Button 2");
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;
	c.gridx = 1;
	c.gridy = 1;
	pane.add(button, c);

	button = new JButton("Button 3");
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;
	c.gridx = 2;
	c.gridy = 1;
	pane.add(button, c);

	button = new JButton("Button 4");
	c.fill = GridBagConstraints.HORIZONTAL;
	//c.ipady = 40;      //make this component tall
	c.insets = new Insets(0, 0, 20, 0);
	c.weightx = 0.0;
	c.gridwidth = 1;
	c.weightx = 0.5;
	c.gridx = 1;
	c.gridy = c.gridy + 1;
	pane.add(flasher, c);

	c.fill = GridBagConstraints.HORIZONTAL;
	c.ipady = 0;       //reset to default
	c.weighty = 1.0;   //request any extra vertical space
	c.anchor = GridBagConstraints.PAGE_END; //bottom of space
	c.insets = new Insets(10,0,0,0);  //top padding
	c.gridx = 1;       //aligned with button 2
	c.gridwidth = 2;   //2 columns wide
	c.gridy = 2;       //third row
	pane.add(button, c);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GridBagLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel background = new JPanel();
        
        JPanel moveable = new JPanel();
        JPanel moveableBackground = new JPanel();
        moveable.setLayout(new GridLayout(1, 1));
        moveable.add(moveableBackground);
        background.add(moveable);
        
        moveable.setPreferredSize(new Dimension(300, 200));
        ComponentMover mover = new ComponentMover(moveable, moveableBackground);
        //Set up the content pane.
        addComponentsToPane(moveableBackground);
        
	    moveable.setBorder(BorderFactory.createLineBorder(Color.black));
	    ComponentResizer cr = new ComponentResizer();
	    cr.setSnapSize(new Dimension(10, 10));
	    cr.registerComponent(moveable);
        
        //moveable.revalidate();

        moveable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        background.add(moveable);
        frame.getContentPane().add(background);
        //Display the window.
        frame.pack();
        frame.setSize(900, 600);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
