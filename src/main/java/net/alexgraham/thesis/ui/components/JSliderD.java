package net.alexgraham.thesis.ui.components;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

import org.omg.CORBA.portable.ValueBase;

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

	
	@Override
	public void setValue(int n) {
		// TODO Auto-generated method stub
		super.setValue(n);
	}


	public double getDoubleValue() {
		return this.getValue() / (double) modifier;
	}
	
	public void setDoubleValue(double value) {
		this.setValue((int)(value * modifier));
	}
}
