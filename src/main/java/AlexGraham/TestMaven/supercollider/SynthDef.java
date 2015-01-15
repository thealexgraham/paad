package AlexGraham.TestMaven.supercollider;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.UUID;

public class SynthDef {
	private String synthName;
	private ArrayList<Parameter> parameters;
	
	SCLang sc;
	UUID id;
	
	public SynthDef(String synthName, SCLang sc) {
		this.sc = sc;
		this.synthName = synthName;
		id = UUID.randomUUID();
		parameters = new ArrayList<SynthDef.Parameter>(); // Blank array for params
		
	}
	public void start() {
		sc.sendMessage("/" + synthName + "/start", id.toString());
	}
	
	public void addParameter(String name, float min, float max, float value) {
		parameters.add(new Parameter(name, min, max, value));
	}
	
	public void changeParameter(String paramName, double value) {
		sc.sendMessage("/" + synthName + "/" + paramName, id.toString(), value);
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
	
	public void close() {
		// Stop the synth at ID
    	sc.sendMessage("/" + synthName + "/stop", id.toString());
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
