~java.addDefinition("RandomPass",
	\taskRunner,
	{
		| object, percent |
		var choice = 100.rand;
		if (choice < percent.value,
			{ object.sendActionMessage; }
		)
	},
	~java.createParamsList(
		~java.addFloat(\percent, 0, 100, 50);
	)
);

~java.addDefinition("RandomDelay",
	\taskRunner,
	{
		| object, waitMin, waitMax |
		var time = rrand(waitMin.value * 1.0, waitMax.value);
		time.wait;
		object.sendActionMessage;
	},
	~java.createParamsList(
		~java.addRange(\wait, \float, 0, 5, 1, 5);
	)
);
~java.addDefinition("DelayAction",
	\taskRunner,
	{
		| object, time |
		time.wait;
		object.sendActionMessage;
	},
	~java.createParamsList(
		~java.addFloat(\time, 0, 5, 0.5);
	)
);

~java.addDefinition("SteadyAction",
	\taskPlayer,
	{
		| object, time |
		loop { // Go forever
			object.sendActionMessage;
			time.value.wait;
		}
	},
	[[\time, 0, 5, 1]]
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


~java.addDefinition(\RandomAction, \taskPlayer,
	{
		| object, minTime, maxTime |
		loop { // Go forever
			var secondsToWait = rrand(minTime.value, maxTime.value);
			object.sendActionMessage; // Could concievably split up into multiple actions
			secondsToWait.wait;
		}
	},
	~java.createParamsList(
		~java.addFloat(\minTime, 0.00, 10.00, 1.00),
		~java.addFloat(\maxTime, 0.00, 10.00, 2.00),
	)
);

