package net.alexgraham.thesis.ui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

public class FlashButton extends JButton {
	private Timer timer;
	public Color blinkColor = Color.RED;
	private Color idleColor = Color.WHITE;
	
	
	public FlashButton(int flashLength, Color blinkColor) {
		this.blinkColor = blinkColor;
		
		setOpaque(true);
		setBorderPainted(false);
		setBackground(idleColor);
		
		
		ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  if(!timer.isRunning())
		    		  setBackground(idleColor);
		    	  else
		    		  setBackground(idleColor);
		      }
		  };
		  
		  timer = new Timer(flashLength, taskPerformer);
		  
		  addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				flash();
			}
		});
	}
	
	public void flash() {
		timer.stop();
		setBackground(blinkColor);
		timer.start();
	}
	
	public void flash(Color color) {
		timer.stop();
		setBackground(color);
		timer.start();
	}
}
