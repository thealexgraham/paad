ModuleType {
	var <>argsDict;
	var <>defName;
	var instanceId;

	*new {
		^super.new;
	}

	init { | id, name, function, arguments |
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

	}

	getParams {
		^argsDict;
	}

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
	}

}