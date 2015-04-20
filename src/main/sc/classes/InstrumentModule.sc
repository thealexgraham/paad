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

		// OutBus needs to be an actual bus
		outBus = Bus.control.set(~java.getMasterIn.index);

		// Make it look like ParameterBus so it can be used the same way
		argsDict.put(\outBus, (bus: Bus.control.set(~java.getMasterIn.index)));
	}

	getParams {
		^argsDict;
	}

/*	getBusses {
		var busDict = Dictionary.new;

		argsDict.keysValuesArrayDo { | name, paramBus |
			busDict.put(name, paramBus.bus);
	}*/

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