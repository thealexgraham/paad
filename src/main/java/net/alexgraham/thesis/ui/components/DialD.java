package net.alexgraham.thesis.ui.components;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.*;

public class DialD extends Dial {
	public DialD() {
		super();
		setModel(new DoubleBoundedRangeModel(4, 0, 1, 0));
	}
	
	public DialD(int decimals, double min, double max, double value) {
		super();
		setModel(new DoubleBoundedRangeModel(decimals, min, max, value));
	}
	
	public DialD(BoundedRangeModel model) {
		super();
		setModel(model);
	}
	
	public void setDoubleValue(double value) {
		((DoubleBoundedRangeModel)getModel()).setDoubleValue(value);
	}
	
	public double getDoubleValue() {
		return ((DoubleBoundedRangeModel)getModel()).getDoubleValue();
	}
	
	@Override
	protected String getValueString() {
		int tempMod = 100;
		return String.valueOf((double)((int)(getDoubleValue() * tempMod)) / tempMod);
	}
}
