PatternGenerator {
	var patternFunction;
	var <>currentPattern;
	var <>argsDict;
	var <>action;
	*new { | function, arguments |
		^super.new.init(function, arguments);
	}

	init { |function, arguments|

		action = function;
		argsDict = Dictionary.new;

		arguments.do({ |item, i|
			var name = item[0];
			var type = item[1];

			switch(type,
				\int, {
					var min = item[2];
					var max = item[3];
					var default = item[4];
					argsDict.put(name, ParameterBus.new(name, default, min, max));
				},
				\choice, {
					var choiceName = item[2][0];
					var value = item[2][1];
					argsDict.put(name, ChoiceParam.new(name, choiceName, value));
				},
				{
					var min = item[2];
					var max = item[3];
					var default = item[4];
					argsDict.put(name, ParameterBus.new(name, default, min, max));
				}
			);

		});
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
}