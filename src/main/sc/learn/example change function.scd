ParamChangeFunctionDef("percentWalk", {
	| value, min, max, maxChangePercent |
	var changeTop, change;

	changeTop = (max - min) * (maxChangePercent / 100);
	change = changeTop.rand * [1, -1].choose;

	^(value + change);
}, [[\maxChangePercent, 0, 100, 50]]);

ParamChangeFunctionDef("fullRandom", {
	| value, min, max |
	^min.rrand(max);
});


Bus
// Every time it is called, it calls the function on each of its connected parameters
// Make sure to keep value between min and max before sending back

// What if the value isn't a normal parameter...need to think about that

3.rrand(5);