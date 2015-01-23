package net.alexgraham.thesis.ui.components;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

import org.omg.CORBA.portable.ValueBase;

public class JSliderD extends JSlider {
	
	public JSliderD(int orientation, int decimals, double min, double max, double value) {
		super(orientation);
		setModel(new DoubleBoundedRangeModel(decimals, min, max, value));
	}
	
	public JSliderD(int orientation, BoundedRangeModel model) {
		super(orientation);
		setModel(model);
	}
	
	public JSliderD(BoundedRangeModel model) {
		super();
		setModel(model);
	}

	public void setDoubleValue(double value) {
		((DoubleBoundedRangeModel)getModel()).setDoubleValue(value);
	}
	
	public double getDoubleValue() {
		return ((DoubleBoundedRangeModel)getModel()).getDoubleValue();
	}
}
