package net.alexgraham.thesis.tests;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.corba.se.impl.orbutil.graph.Node;

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
		    panel.setLayout(new GridLayout(0, 1));

		    JButton button = new JButton("Outout");

		    panel.add(env);		    
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
		    frame.setSize(300, 200);
		    frame.setVisible(true);
		    
	    	System.in.read();
	    	env.sendDirtyValues(0);

		  }
}
