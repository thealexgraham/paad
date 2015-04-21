ParamBuilder {
	*createParamsList { arg ...args;
		var params = [];
		args.do({ |item, i|
			if (item[0] == \range,
				{ params = params.addAll([item[1], item[2]]); },
				{ params = params.add(item)}
			);
		});
		^params;
	}
	*float { | name, min, max, default |
		^[name, \float, min, max, default];
	}
	*choice { |name, defaultName, defaultValue|
		^[name, \choice, [defaultName, defaultValue]];
	}
	*int { |name, min, max, default |
		^[name, \int, min, max, default];
	}
	*range { |name, type, min, max, lower, upper|
		^[\range, [(name++"Min"), type, min, max, lower],[(name++"Max"), type, min, max, upper]];
	}

}