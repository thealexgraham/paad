package net.alexgraham.thesis.supercollider.synths;

public class Parameter {
	public String name;
	public double min;
	public double max;
	public double value;
	
	public Parameter(String name, double min, double max, double value) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getValue() {
		return value;
	}

}