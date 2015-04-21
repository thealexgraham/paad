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
			var type = item[1];

			switch(type,
				\int, {
					var min = item[2];
					var max = item[3];
					var default = item[4];
					var paramBus = ParameterBus.new(name, default, min, max);
					argsDict.put(name, paramBus);
					paramBus.ownerId = id;
				},
				\choice, {
					var choiceName = item[2][0];
					var value = item[2][1];
					var choiceType = item[3];
					var choiceParam;
					if (choiceType.isCollection != true, // If its a single number it can be a bus
						{choiceParam = ChoiceParamBus.new(name, choiceName, value); },
						{choiceParam = ChoiceParam.new(name, choiceName, value); }
					);
					argsDict.put(name, choiceParam);
					choiceParam.ownerId = id;
				},
				\float, {
					var min = item[2];
					var max = item[3];
					var default = item[4];
					var paramBus = ParameterBus.new(name, default, min, max);
					argsDict.put(name, paramBus);
					paramBus.ownerId = id;
				},
				{
					("No param found for "++item).postln;
				}
			);
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