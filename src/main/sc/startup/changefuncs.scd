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

~java.addDefinition("SteadyAction",
	\taskPlayer,
	{
		| object, timeBetween |
		loop { // Go forever
			object.sendActionMessage;
			timeBetween.value.wait;
		}
	},
	[[\timeBetween, 0, 10, 1]]
);

~java.addDefinition("SimpleAction",
	\taskRunner,
	{
		| object |
		object.sendActionMessage;
	},
);

~java.addDefinition("SimpleAction",
	\taskRunner,
	{
		| object |
		object.sendActionMessage;
	}
);

~java.addDefinition("RandomGate",
	\taskRunner,
	{
		| object |
		var choice = object.numberOfActionOuts.rand;
		object.sendActionMessage(choice);
	},
/*	~java.createParamsList(
		~java.addActionOut(\one),
		~java.addActionOut(\two),
		~java.addActionOut(\three),
	)*/
);


~java.addDefinition("RandomAction",
	\taskPlayer,
	{
		| object, minSeconds, maxSeconds |
		loop { // Go forever
			var secondsToWait = rrand(minSeconds.value, maxSeconds.value);
			object.sendActionMessage; // Could concievably split up into multiple actions
			secondsToWait.wait;
		}
	},
	[[\minSeconds, 0, 10, 1],
		[\maxSeconds, 0, 10, 2]]
);





