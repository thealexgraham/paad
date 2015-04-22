~java.addDefinition("SimpleScale", \patternGen,
	{
		|lengthMin, lengthMax, scale, root, octaveMin, octaveMax|

		var durations = [[1, 1], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];

		var finalPattern;

		var length = rrand(lengthMin, lengthMax);
		var notes = Array.fill(length, { |i|
			var octaves = (octaveMin..octaveMax);
			var degree = scale.choose;
			var octave = octaves.choose;
			(root + degree) + (octave * 12);
		});

		var durationPattern = Array.newFrom(durations.choose);
		durationPattern.postln;

		finalPattern = Array.fill(length, { |i|
			var currentDuration;

			if (durationPattern.size == 0, {
				durationPattern = Array.newFrom(durations.choose);
			});

			currentDuration = durationPattern[0];
			durationPattern.removeAt(0);

			[notes[i], currentDuration];
		});

		finalPattern = finalPattern.add([\rest, 2]);
		finalPattern;
	},

	~java.createParamsList(
		~java.addChoice(\scale, "pentatonic", [0,3,5,6,10], [\degrees]),
		~java.addInt(\root, 0, 127, 62),
		~java.addInt(\octaveMin, -5, 5, 0),
		~java.addInt(\octaveMax, -5, 5, 1),
		~java.addRange(\length, \int, 0, 20, 5, 9),
		~java.setReturnType( [[\midi],[\dur]] );
	),

);

~java.addDefinition("Tester", \patternGen,
	{
		|scale, root|

		scale.postln;

		[[],[]];
	},
	[
		~java.addChoice(\scale, "pentatonic", [0,3,5,6,10], [\degrees]),
	]
);


~java.addDefinition("RandomlyRandom", \patternGen,
	{
		|lengthMin, lengthMax, noteMin, noteMax|

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
		finalPattern;
	},

	~java.createParamsList(
		~java.addRange(\note, \int, 35, 120, 45, 92),
		~java.addRange(\length, \int, 0, 20, 5, 9),

		~java.setReturnType( [[\midi],[\dur]] );
	),
);

~java.addDefinition("RandomPitches", \patternGen,
	{
		|lengthMin, lengthMax, noteMin, noteMax|

		var length = rrand(lengthMin, lengthMax);
		var notes = Array.fill(length, { |i|
			rrand(noteMin, noteMax);
		});

		notes;
	},

	~java.createParamsList(
		~java.addRange(\note, \int, 35, 120, 45, 92),
		~java.addRange(\length, \int, 0, 20, 5, 9),
		~java.setReturnType( [\midi] );
	),
);

~java.addDefinition("DurCycles", \chooser,
	{
		var output = [];
		var durations = [0.21, 0.25, 0.33, 0.2, 0.75, 0.3];
		output = Array.fill(8, { |i|
		[((i+1).asString ++ " durs"), Array.fill(i+1, {durations.choose})];
		});
		output;
	},
	~java.createParamsList(
		~java.setReturnType( [\dur] );
	)
);

~java.addDefinition("ScalesInt", \chooser,
	{
		[
			["pentatonic", [0, 3, 5, 7, 10]],
			["Major!!!", [0, 2, 4, 5, 7, 9, 11]],
			["Minor", [0, 2, 3, 5, 10, 11]],
			["Chromatic", [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]],
			["Ones", [0, 1]],
			["Twos", [0, 1, 2, 3]]
		]
	},
	~java.createParamsList(
		~java.setReturnType( [\degrees] );
	)
);




~java.addDefinition("RoutinePlayer",
	\routinePlayer,
	{
		| player, pattern |
		Prout({| ev |
			var pat;
			block { |break|
				loop {
					pattern.value.class.postln;
					"Playing something".postln;
					pat = Pbindf(*[
						player.template, // Connected instrument data
						#[\midinote, \dur], Pseq(pattern.value) // the current pattern's value
					]);
					"Doing a pattern".postln;
					player.doPlayedAction; // Run the action to do when played (probably OSC message)
					ev = pat.embedInStream(ev); // Embed the pattern and wait for it to be played
				}
			}
		});
	},
	[
		~java.addChoice(\pattern, "default", [[60,1], [60,0.5], [61,0.5], [60,1]], [[\midi],[\dur]]);
		//[\pattern, \choice, ["pentatonic", [[60,1], [60,0.5], [61,0.5], [60,1]]]]
	]
);

~java.addDefinition("SeparatePlayer",
	\routinePlayer,
	{
		| player, pitches, durations |
		Prout({| ev |
			var pat;
			block { |break|
				loop {
					pat = Pbindf(*[
						player.template, // Connected instrument data
						\midinote, Pseq(pitches.value), // the current pattern's value
						\dur, Pseq(durations.value)
					]);
					"Doing a pattern".postln;
					player.doPlayedAction; // Run the action to do when played (probably OSC message)
					ev = pat.embedInStream(ev); // Embed the pattern and wait for it to be played
				}
			}
		});
	},
	[
		~java.addChoice(\pitches, "default", [], [\midi]),
		~java.addChoice(\durations, "default", [], [\dur])
		//[\pattern, \choice, ["pentatonic", [[60,1], [60,0.5], [61,0.5], [60,1]]]]
	]
);




// ~java.newChooser(\scale,
// 	[["pentatonic", [0, 3, 5, 7, 10]],
// 		["minor", [0, 1, 2, 3, 4]],
// 	["major", [0, 5, 6, 9, 10]]]
// );
