EffectModule : ModuleType {
	var <>synth;
	var <>inBus;
	var outBus;


	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}

	*newMaster { |id, name, function, arguments |
		^super.new.initMaster(id, name, function, arguments);
	}

	init { | id, name, function, arguments |

		super.init(id, name, function, arguments);

		inBus = Bus.audio(Server.default, 2);
		outBus = ~java.getMasterIn;

		synth = Synth.head(~effectsGroup, name, [\inBus, inBus, \outBus, ~java.getMasterIn.index]);

		this.getParams.keysValuesDo({ |key, value|
			synth.map(key, value.bus);
		});

		synth.set(\outBus, outBus);
	}

	initMaster { | id, name, function, arguments |
		this.init(id, name, function, arguments);
		~masterFader = synth;
		synth.set(\inBus, ~java.getMasterIn);
		synth.set(\outBus, 0);
	}

	removeSelf {
		super.removeSelf;
		inBus.free;
		synth.free;
	}

}