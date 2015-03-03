~java.addDefinition("PercentWalk",
	\changeFunc,
	{
		| value, min, max, walkAmount |

		var changeTop, change;
		"performing change func".postln;
		changeTop = (max - min) * (walkAmount / 100);
		change = changeTop.rand * [1, -1].choose;
		value + change;
	},
	[[\walkAmount, 0, 100, 10]]

);

~java.addDefinition("TrueRandom",
	\changeFunc,
	{
		| value, min, max |
		rrand(min, max);
	}
);