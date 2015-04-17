TopParam {


	var <>argsDict;
	var state;
	var params;

	*new { | name id |
		^super.new.init;
	}


	init { | name, id |

		params = Dictionary.new;

/*		arguments.do({ |item, i|
			var name = item[0];
			var min = item[1];
			var max = item[2];
			var default = item[3];
			argsDict.put(name, ParameterBus.new(name, default, min, max));
		});*/

	}

	changeEnv { | param, envValues, envTimes |
		params.put(param, Env.new(envValues, envTimes));
	}

	addParam { |param, envValues, envTimes|
		params.put(param, Env.new(envValues, envTimes));
	}

	removeParam { |param|
		params.removeAt(param);
	}

	changeState { |val|
		state = val;
		params.keysValuesDo({ |param, env|
			param.set(env.at(state));
		});
	}
}