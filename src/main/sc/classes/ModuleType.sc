ModuleType {
	var <>argsDict;
	var <>defName;
	var instanceId;

	*new {
		^super.new;
	}

	*setupParams { | id, params |
		var argsDict = Dictionary.new;

		params.do({ |item, i|
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
					var choiceName = item[2];
					var value = item[3];
					var choiceType = item[4];
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
		^argsDict;
	}

	init { | id, name, function, arguments |
		instanceId = id;
		argsDict = ModuleType.setupParams(id, arguments);

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

	setParamLive { |paramName, value|
		argsDict.at(paramName).set(value);
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free
		});
	}

}