package AlexGraham.TestMaven.supercollider;

import java.util.ArrayList;

public class SynthDef {
	private String synthName;
	private ArrayList<Parameter> parameters;
	
	public SynthDef(String synthName) {
		this.synthName = synthName;
		parameters = new ArrayList<SynthDef.Parameter>();
	}
	
	public void addParameter(String name, float min, float max, float value) {
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
		public float min;
		public float max;
		public float value;
		
		public Parameter(String name, float min, float max, float value) {
			this.name = name;
			this.min = min;
			this.max = max;
			this.value = value;
		}
	}
}
