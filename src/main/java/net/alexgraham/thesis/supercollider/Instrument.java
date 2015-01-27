package net.alexgraham.thesis.supercollider;

import com.sun.xml.internal.bind.v2.model.core.ID;

import net.alexgraham.thesis.App;



public class Instrument extends Synth {


	public Instrument(SynthDef synthDef, SCLang sc) {
		super(synthDef, sc);
		init();
		this.start();

	}
	
	public Instrument(SynthDef synthDef, SCLang sc, String name) {
		this(synthDef, sc);
		this.name = name;
		init();
		this.start();
	}
	
	public void init() {
		startCommand = "/inst/add";
		paramChangeCommand = "/inst/paramc";
		closeCommand = "/inst/remove";
	}

	public void runInstrumentTest() {
		updateParameter("gain", 0.7);
		App.sc.sendMessage("/inst/playtest", getSynthName(), getID());
	}
	
	@Override
	public void changeParameter(String paramName, double value) {
		// TODO Auto-generated method stub
		super.changeParameter(paramName, value);
		System.out.println("Instrument is changing parameter");
	}

}
