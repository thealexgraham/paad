EffectModule {
	var <>argsDict;
	var <>synth;
	var instanceId;
	var name;
	var inBus;
	var outBus;


	*new { | id, function, arguments |
		^super.new.init(id, function, arguments);
	}

	*newMaster { |id, function, arguments |
		^super.new.initMaster(id, function, arguments);
	}

	init { | id, function, arguments |

		instanceId = id;
		argsDict = Dictionary.new;

		arguments.do({ |item, i|
			var name = item[0];
			var min = item[1];
			var max = item[2];
			var value = item[3];
			var paramBus = ParameterBus.new(name, value, min, max);
			paramBus.ownerId = instanceId;
			argsDict.put(name, paramBus);
		});

		inBus = Bus.audio(Server.default, 2);
		outBus = ~java.getMasterIn;

		synth = SynthDef.play(~effectsGroup [\inBus, inBus, \outBus, ~java.getMasterIn.index]);

		argsDict.keysValuesDo({ |key value|
			synth.map(key, value.bus);
		});

		synth.set(\outBus, outBus);
	}

	initMaster { | id, function, arguments |
		init(id, function, arguments);
		~masterFader = synth;
		synth.set(\inBus, ~java.getMasterIn);
		synth.set(\outBus, 0);
	}

	paramAt { |paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		argsDict.at(paramName).setSilent(value);
	}

	removeSelf {
		inBus.free;
		synth.free;
	}

}