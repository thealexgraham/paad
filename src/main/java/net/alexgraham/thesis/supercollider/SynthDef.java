package net.alexgraham.thesis.supercollider;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SynthDef {
	
	private String synthName;
	private ArrayList<Parameter> parameters;
	
	SCLang sc;
	
	public SynthDef(String synthName, SCLang sc) {
		this.sc = sc;
		this.synthName = synthName;
		
		parameters = new ArrayList<SynthDef.Parameter>(); // Blank array for params
		
	}
	
	public void addParameter(String name, double min, double max, double value) {
		parameters.add(new Parameter(name, min, max, value));
		
	}

	public String getSynthName() {
		return synthName;
	}

	public void setSynthName(String synthName) {
		this.synthName = synthName;
	}
	
	public ArrayList<Parameter> getParameters() {
		return parameters;
	}
	
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
}
