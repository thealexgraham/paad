package net.alexgraham.thesis.ui.components;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.*;

public class DialD extends Dial {
	private int modifier;
	
	public DialD(int decimals, double min, double max, double value) {
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
	
	@Override
	protected String getValueString() {
		int tempMod = 100;
		return String.valueOf((double)((int)(getDoubleValue() * tempMod)) / tempMod);
	}
}
