package net.alexgraham.thesis.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextMeasurer;
import java.io.IOException;

import javafx.scene.layout.Border;
import javafx.scene.paint.ColorBuilder;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.omg.CORBA.PUBLIC_MEMBER;

import net.alexgraham.thesis.ui.helpers.ComponentMover;

public class SelectablePanel extends JPanel {
	private boolean selected = false;
	public SelectablePanel() {
		// TODO Auto-generated constructor stub
		super();
		setBorder(BorderFactory.createDashedBorder(Color.black));
		
	}
	
	public void setupListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				//if (e.getButton() == MouseEvent.MOUSE_CLICKED) {
					System.out.println("Changing border");
					System.out.println(getLocation().toString());

					if (selected) {

						setBorder(BorderFactory.createLineBorder(Color.black));
						selected = false;
						System.out.println("Border Change " + getLocation().toString());

						//setLocation(getLocation());
					} else {
						setBorder(BorderFactory.createLineBorder(Color.black, 2));
						System.out.println("Border change + " + getLocation().toString());

						//setLocation(getLocation());
						selected = true;
					}
				//}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				//super.mouseClicked(e);

				//super.mouseClicked(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseReleased(e);
				System.out.println("Border change mouse release " + getLocation().toString());
				
			}
		});
	}
	public static void main(String[] args) throws IOException {
	    JFrame frame = new JFrame();

	    JPanel panel = new JPanel();
	    
	    SelectablePanel textMove = new SelectablePanel();
	    
	    textMove.setLayout(new BoxLayout(textMove, BoxLayout.Y_AXIS));
	    JLabel top = new JLabel("Drag Me");
	    textMove.add(top);
	    panel.add(textMove);
	    ComponentMover cm = new ComponentMover();
	    cm.registerComponent(textMove);
	    textMove.setupListeners();
	    frame.getContentPane().add(panel);
	    frame.setSize(300, 200);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	System.in.read();
    	textMove.setBorder(BorderFactory.createLineBorder(Color.black));
    	System.out.println("Changed border");
	}
}