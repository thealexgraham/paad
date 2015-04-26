

~java.addDefinition("RandomPattern",
	\taskRunner,
	{
		|object, lengthMin, lengthMax, noteMin, noteMax|

		var durations = [1, 0.25, 0.33, 0.5, 0.75, 1.5];

		var finalPattern;

		var length = rrand(lengthMin, lengthMax);
		var notes = Array.fill(length, { |i|
			rrand(noteMin, noteMax);
		});

		finalPattern = Array.fill(length, { |i|
			[notes[i], durations.choose];
		});

		finalPattern = finalPattern.add([\rest, 2]);

		object.return(\pattern, finalPattern);
		object.sendActionMessage(\generated);
	},
	~java.createParamsList(
		~java.addRange(\seconds, \float, 0, 10, 1, 2),
		~java.addActionOutput(\generated),
		~java.addReturn(\pattern, [[\midi],[\dur]]);
);


