PatternGenerator {
	var patternFunction;
	var currentPattern;
	var <>argsDict;
	var <>action;
	var listeners;

	*new { | id, function, arguments |
		^super.new.init(id, function, arguments);
	}

	init { |id, function, arguments|

		listeners = IdentitySet.new;
		argsDict = Dictionary.new;

		arguments.do({ |item, i|
			var name = item[0];
			var type = item[1];

			switch(type,
				\int, {
					var min = item[2];
					var max = item[3];
					var default = item[4];
					var paramBus = ParameterBus.new(name, default, min, max);
					argsDict.put(name, paramBus);
					paramBus.ownerId = id;
					postln("Putting in " + paramBus);
				},
				\choice, {
					var choiceName = item[2];
					var value = item[3];
					var choiceParam = ChoiceParam.new(name, choiceName, value);
					argsDict.put(name, choiceParam);
					choiceParam.ownerId = id;
				},
				{
					var min = item[2];
					var max = item[3];
					var default = item[4];
					var paramBus = ParameterBus.new(name, default, min, max);
					argsDict.put(name, paramBus);
					paramBus.ownerId = id;
				}
			);

		});

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


	// Shared
	paramAt { |paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		var param = argsDict.at(paramName);
		if (param.class != ChoiceParam,
			{ param.setSilent(value); });
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free;
		});
	}
}
