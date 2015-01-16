package net.alexgraham.thesis.ui.components;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class JSliderD extends JSlider {
	private int modifier;
	
	public JSliderD(int orientation, int decimals, double min, double max, double value) {
		super(orientation);
		
		// Modifier is what we multiply divide by
		modifier = (int) Math.pow(10, decimals);
		
		// Set up the values
		this.setMinimum((int)(min * modifier));
		this.setMaximum((int)(max * modifier));
		this.setValue((int)(value * modifier));
		
	}
	
	public double getDoubleValue() {
		return this.getValue() / (double) modifier;
	}
}
