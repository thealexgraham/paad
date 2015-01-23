package net.alexgraham.thesis.ui.components;

import javax.swing.DefaultBoundedRangeModel;

public class DoubleBoundedRangeModel extends DefaultBoundedRangeModel {
	
	private int modifier;
	
	public DoubleBoundedRangeModel(int decimals, double min, double max, double value) {
		super();
		// Modifier is what we multiply divide by
		modifier = (int) Math.pow(10, decimals);
		
		// Set up the values
		this.setMinimum((int)(min * modifier));
		this.setMaximum((int)(max * modifier));
		this.setValue((int)(value * modifier));
	}
	
	public void setDoubleValue(double value) {
		this.setValue((int) (value * modifier));
	}
	
	public double getDoubleValue() {
		return this.getValue() / (double) modifier;
	}
	
	public void setDoubleMinimum(double minimum) {
		this.setMinimum((int) (minimum * modifier));
	}
	
	public double getDoubleMinimum() {
		return this.getMinimum() / (double) modifier;
	}
	
	public void setDoubleMaximum(double maximum) {
		this.setMaximum((int) (maximum * modifier));
	}
	
	public double getDoubleMaximum() {
		return this.getMaximum() / (double) modifier;
	}
}
