ChangeFunc {

	var <>action;
	var <>argsDict;
	var instanceId;
	var listeners;

	*new { |id, function, arguments |
		^super.new.init(id, function, arguments);
	}


	init { |id, function, arguments|

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
		
		listeners = IdentitySet.new;
		action = function;
	}

	paramAt { |paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		argsDict.at(paramName).setSilent(value);
	}

	addListener { |obj|
		listeners.add(obj);
	}

	removeListener { |obj|
		listeners.remove(obj);
	}

	// Go through to all our connections and perform the function on them
	doAction {
		listeners.do { |item, i|
			this.doChange(item);
		}
	}

	doChange { |parameter|
		var args = [\value, parameter.value, \min, parameter.min, \max, parameter.max];
		var newValue;

		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val.value]);
		});
		// Perform the action and get the new value
		newValue = action.performKeyValuePairs(\value, args);

		// Clip the new value
		newValue = newValue.clip(parameter.min, parameter.max);

		// Set the parameter to the new value
		parameter.set(newValue);
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free
		});
	}
}


