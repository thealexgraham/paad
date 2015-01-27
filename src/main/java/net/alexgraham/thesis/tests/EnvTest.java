package net.alexgraham.thesis.tests;

import javax.swing.JFrame;

import net.alexgraham.thesis.ui.components.swingosc.EnvelopeView;

public class EnvTest {
	  public static void main(String[] argv) {
		    JFrame frame = new JFrame();
		    EnvelopeView env = new EnvelopeView();
		    env.setValues(new float[]{0.0f, 0.1f, 0.2f, 1f}, new float[]{0.0f, 0.5f, 1.0f, 0.5f});
		    frame.getContentPane().add(env);
		    frame.setSize(300, 200);
		    frame.setVisible(true);

		  }
}
