InstrumentModule : ModuleType {
	var <>synth;
	var outBus;


	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}


	init { | id, name, function, arguments |
		super.init(id, name, function, arguments);

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

	setParamLive { |paramName, value|
		argsDict.at(paramName).set(value);
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free
		});
	}

}