ParamChangeFunctionDef("percentWalk", {
	| value, min, max, maxChangePercent |
	var changeTop, change;

	changeTop = (max - min) * (maxChangePercent / 100);
	change = changeTop.rand * [1, -1].choose;

	^(value + change);
}, [[\maxChangePercent, 0, 100, 50]]);

newChangeFunction { |function, arguments|
	action = function;
	argsDict = Dictionary.new;
	arguments.do({ |item, i|
		var name = item[0];
		var min = item[1];
		var max = item[2];
		var default = item[3];
		argsDict.put(name, ParameterBus.new(name, default, min, max));
	});
}


doChange { |parameter|
	var args = [\value, parameter.value, \min, parameter.min, \max, parameter.max];
	var value;

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

a = 10;
a.clip(20, 100);
a


ParamChangeFunctionDef("fullRandom", {
	| value, min, max |
	^min.rrand(max);
});

10.clip(4, 5)

3.value
// Every time it is called, it calls the function on each of its connected parameters
// Make sure to keep value between min and max before sending back

// What if the value isn't a normal parameter...need to think about that

3.rrand(5);

(
var args = [\whatever, 1, \two, 2];
d = Dictionary.new;
d.put(\maxChangePercent, ParameterBus.new(\maxChangePercent, 100, 0, 50));
d.put(\other, ParameterBus.new(\whatever, 10, 0, 5));
d.pairsDo({|key, val| args = args.addAll([key, val.value]);});
args
)

(
a = { |a, b, c| postf("% plus % plus % is %\n", a, b, c, a + b + c); "" };
a.performKeyValuePairs(\value, [\a, 1, \b, 2, \c, 3, \d, 4]);

)


// do, collect, reject, select
d = Dictionary[\a -> "hello", \b -> "robot", \c -> [1, 2, 3]];
d = (a: "hello", b: "robot", c: [1, 2, 3]); // equivalent
d.do { |item, i| [item, i].postln };
d.collect { |item| item + 100 };
d.reject { |item| item.size > 4 };
d.select { |item| item.size > 4 };

