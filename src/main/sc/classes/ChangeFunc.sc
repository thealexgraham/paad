ChangeFunc : ModuleType {

	var <>action;
	var listeners;

	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}


	init { |id, name, function, arguments|
		super.init(id, name, function, arguments);

		listeners = IdentitySet.new;
		action = function;
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
		var args = [\value, parameter.value, \min, parameter.min, \max, parameter.max, \default, parameter.default];
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
		super.removeSelf;
	}
}


