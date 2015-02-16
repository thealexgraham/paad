package net.alexgraham.thesis.tests.panels;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.alexgraham.thesis.ui.components.OldMoveablePanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class GridbagMoveable {
	
    final boolean shouldFill = true;
    final boolean shouldWeightX = true;
    final boolean RIGHT_TO_LEFT = false;
    
	class PopUpTest extends JPopupMenu {
	    JMenuItem anItem;
	    public PopUpTest() {
	        anItem = new JMenuItem("Click Me!");
	        anItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("It was done");
				}
			});
	        add(anItem);
	    }
	}
	
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		System.out.println("Set Location Called to " + x + " " + y);
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		System.out.println(stackTraceElements[2]);
	}
	
	
	
    public void setup(Container pane) {
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
		
		JPanel testPanel = new JPanel();
		//testPanel.setLayout(new GridLayout(1, 1));
		JLabel titleLabel = new JLabel("title");
		
		titleLabel.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e){
	        	GridbagMoveable.this.getInterior().dispatchEvent(e);;

		    }
		    
		    public void mouseReleased(MouseEvent e) {
		    	if (e.isPopupTrigger()) {
		    		doPop(e);
		    	}

		    }
		    
		    public void mouseClicked(MouseEvent e) {
		        if(e.getClickCount()==2){
		            System.out.println("Let me change");
		        }
	            GridbagMoveable.this.dispatchEvent(e);

		    }

		    private void doPop(MouseEvent e){
		        PopUpTest menu = new PopUpTest();
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }
		});
		
		testPanel.add(titleLabel);
		testPanel.setBackground(Color.LIGHT_GRAY);
		testPanel.setOpaque(true);
		//testPanel.setAlignmentX(0.5f);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(testPanel, c);
	
		button = new JButton("Button 1");
		if (shouldWeightX) {
		c.weightx = 0.5;
		}
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
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
		pane.add(button, c);
	
		button = new JButton("5");
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
    
    public static void main(String[] args) {
    	
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("GridBagLayoutDemo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel background = new JPanel();
                background.setOpaque(true);
                background.setBackground(Color.white);
                background.setLayout(null);

                background.setBorder(BorderFactory.createLineBorder(Color.black));
                GridbagMoveable moveable = new GridbagMoveable(300, 200);
                moveable.setLocation(5, 6);
                moveable.setSize(300,200);
                background.add(moveable);
                

                frame.getContentPane().add(background);
                //Display the window.
                frame.setLocationByPlatform(true);
                frame.pack();
                frame.setSize(900, 600);
                frame.setVisible(true);

            }
        });

	}
}
