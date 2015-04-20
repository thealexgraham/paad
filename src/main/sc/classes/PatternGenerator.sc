PatternGenerator {
	var patternFunction;
	var currentPattern;
	var <>argsDict;
	var <>action;

	*new { | id, function, arguments |
		^super.new.init(id, function, arguments);
	}

	init { |id, function, arguments|


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
					var choiceName = item[2][0];
					var value = item[2][1];
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
	}

	getCurrentPattern {
		^currentPattern;
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
	}
}
