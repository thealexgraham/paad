package net.alexgraham.thesis.supercollider.synths.parameters;

public class DoubleParam implements Param, java.io.Serializable {
	public String name;
	public double min;
	public double max;
	public double value;
	
	public DoubleParam(String name, double min, double max, double value) {
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