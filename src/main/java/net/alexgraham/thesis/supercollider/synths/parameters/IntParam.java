package net.alexgraham.thesis.supercollider.synths.parameters;

public class IntParam implements Param {
	public String name;
	public int min;
	public int max;
	public int value;
	
	public IntParam(String name, int min, int max, int value) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getValue() {
		return value;
	}
}
