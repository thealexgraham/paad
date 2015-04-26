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
