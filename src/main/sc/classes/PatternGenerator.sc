PatternGenerator : ModuleType {
	var patternFunction;
	var currentPattern;
	var <>action;
	var listeners;

	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}


	init { |id, name, function, arguments|
		super.init(id, name, function, arguments);

		listeners = IdentitySet.new;
		action = function;
		this.doGenerate;
	}

	doAction {
		this.doGenerate;
	}

	doGenerate {
		var args = Array.new;
		var newPattern;
		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val.value]);
		});
		// Perform the action and get the new value
		newPattern = action.performKeyValuePairs(\value, args);

		currentPattern = newPattern;
		this.updateListeners;
	}

	// update the patterns in a dict
	// Send out the patterns

	getCurrentPattern {
		^currentPattern;
	}

	addListener { |obj|
		listeners.add(obj);
		// Update the param choice now
		this.updateParamChoice(obj); // DO this?
	}

	removeListener { |obj|
		listeners.remove(obj);
	}

	updateListeners {
		listeners.do({ |item, i|
			this.updateParamChoice(item);
		});
	}

	updateParamChoice { | item |
		var currentChoice = this.getCurrentPattern;
		item.set("Connected", this.getCurrentPattern); // name, value
	}

	removeSelf {
		super.removeSelf;
	}
}
