~java.addDefinition("PercentWalk",
	\changeFunc,
	{
		| value, min, max, walkPercent |

		var changeTop, change;
		changeTop = (max - min) * (walkPercent / 100);
		change = changeTop.rand * [1, -1].choose;
		value + change;
	},
	[[\walkPercent, 0, 100, 10]]

);

~java.addDefinition("PercentSwitch",
	\changeFunc,
	{
		| value, default, min, max, changePercent |

		var changeTop, change, range, direction;
		if (value > default,
			{
				direction = -1;
				range = abs(min - default);
			},
			{
				direction = 1;
				range = abs(max - default);
			}
		);
		changeTop = range * (changePercent / 100);
		change = changeTop.rand * direction;
		default + change;
	},
	[[\changePercent, 0, 100, 10]]
);

// Chance to go up or down

~java.addDefinition("TrueRandom",
	\changeFunc,
	{
		| value, min, max |
		rrand(min, max);
	}
);

~java.addDefinition("Default",
	\changeFunc,
	{
		| value, min, max, default |
		default;
	}
);




