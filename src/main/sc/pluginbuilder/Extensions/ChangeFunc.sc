ChangeFunc {

	var <>action;
	var <>argsDict;
	var listeners;

	*new { | function, arguments |
		^super.new.init(function, arguments);
	}


	init { |function, arguments|

		action = function;
		argsDict = Dictionary.new;
		listeners = IdentitySet.new;

		arguments.do({ |item, i|
			var name = item[0];
			var min = item[1];
			var max = item[2];
			var default = item[3];
			argsDict.put(name, ParameterBus.new(name, default, min, max));
		});

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
}


