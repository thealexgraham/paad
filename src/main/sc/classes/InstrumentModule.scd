InstrumentModule {
	var <>argsDict;
	var <>synth;
	var instanceId;
	var outBus;
	var defName;


	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}

	init { | id, name, function, arguments |

		// Probably shared
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

		defName = name;

		// Actual synth stuff
		argsDict.put(\outBus, (bus: Bus.control.set(~java.getMasterIn.index));

		synth.postln;
	}

	// Shared
	paramAt { |paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		argsDict.at(paramName).setSilent(value);
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free
		});

		synth.free;
	}

}