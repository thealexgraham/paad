SynthModuleInherit : ModuleType {
	var <>synth;
	var outBus;

	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}


	init { | id, name, function, arguments |
		super.init(id, name, function, arguments);

		// Actual synth stuff
		outBus = ~java.getMasterIn;

		synth = Synth.head(~synthsGroup, name, [\outBus, ~java.getMasterIn.index]);

		this.getParams.keysValuesDo({ |key, value|
			synth.map(key, value.bus);
		});
	}

	removeSelf {
		super.removeSelf;
		synth.free;
	}

}