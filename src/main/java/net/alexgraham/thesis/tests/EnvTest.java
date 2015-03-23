package net.alexgraham.thesis.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javafx.beans.property.FloatProperty;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.math.NumberUtils;

import net.alexgraham.thesis.ui.components.swingosc.EnvelopeView;



public class EnvTest {
	  public static void main(String[] argv) throws IOException {
		    JFrame frame = new JFrame();
		    EnvelopeView env = new EnvelopeView();
		    env.setValues(new float[]{0.0f, 0.1f, 0.2f, 1f}, 
		    		new float[]{0.0f, 0.5f, 1.0f, 0.5f});
//		    		new int[]{5, 5, 5, 5}, new float[]{-2f,0.5f,0.5f,0.5f});
		    env.setHorizontalEditMode(EnvelopeView.HEDIT_CLAMP);
		    env.setSelectionColor(Color.red);
		    env.sendDirtyValues(0);
		    JPanel panel = new JPanel();
		    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		    JButton button = new JButton("Output");
		    panel.add(env);		    
		    
		    panel.add(createAdjustPanel(env));
		    panel.add(button);
		    button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					env.getEnvelopeValues();
				}
			});
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(panel);
		    frame.setSize(400, 500);
		    frame.setVisible(true);
		    
	    	System.in.read();
	    	env.sendDirtyValues(0);
	  }
	  
	  public static class RangeListener implements KeyListener, ActionListener, FocusListener {
		  
		  private JTextField minField;
		  private JTextField maxField;
		  private String axis;
		  private EnvelopeView env;
		  
		public RangeListener(JTextField minField, JTextField maxField,
				String axis, EnvelopeView env) {
			super();
			this.minField = minField;
			this.maxField = maxField;
			this.axis = axis;
			this.env = env;
		}

			@Override
			public void keyTyped(KeyEvent e) {

			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateRange();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				updateRange();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateRange();
			}
			
			public void updateRange() {
				float[] range = getRangeFromFields(minField, maxField);
				env.setRange(axis, range);
				env.repaint();
			}
	  }
	  
	  public static JPanel createRangePanel(EnvelopeView env, String axis) {
		  JPanel pair = new JPanel();
		  pair.setLayout(new GridLayout(3, 2));
		  
		  float[] range = env.getRange(axis);
		  JTextField minField = new JTextField(4);
		  minField.setText(String.valueOf(range[0]));
		  
		  JTextField maxField = new JTextField(4);
		  maxField.setText(String.valueOf(range[1]));
		  
		  JButton setButton = new JButton("Set");
		  
		  RangeListener listener = new RangeListener(minField, maxField, axis, env); 

		  setButton.addActionListener(listener);
		  minField.addFocusListener(listener);
		  maxField.addFocusListener(listener);
		  
		minField.addKeyListener(listener);
		maxField.addKeyListener(listener);
		  

		  pair.add(new JLabel(axis + "min: "));
		  pair.add(minField);
		  pair.add(new JLabel(axis + " max: "));
		  pair.add(maxField);
		  pair.add(setButton);
		  
		  return pair;
	  }
	  
	  public static JPanel createAdjustPanel(EnvelopeView env) {
		  JPanel main = new JPanel();
		  main.setLayout(new GridLayout(1, 0));
			  
		  main.add(createRangePanel(env, "x"));
		  main.add(Box.createHorizontalGlue());
		  main.add(createRangePanel(env, "y"));

				  
		  return main;
	  }
	  
	  public static float[] getRangeFromFields(JTextField minField, JTextField maxField) {
			if (!NumberUtils.isNumber(minField.getText())) {
				minField.setText("0");
			}
			
			if (!NumberUtils.isNumber(maxField.getText())) {
				maxField.setText("1");
			}
			
			float newMin = Float.valueOf(minField.getText());
			float newMax = Float.valueOf(maxField.getText());
			
			float[] range = {newMin, newMax};
			
			return range;
	  }
	  
	  
}
