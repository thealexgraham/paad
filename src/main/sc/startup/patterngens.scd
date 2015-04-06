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
	[[\lengthMin, \int, 0, 10, 4],
		[\lengthMax, \int, 0, 16, 8],
		[\scale, \choice, ["pentatonic", [0, 3, 5, 7, 10]]], //??
		[\root, \int, 0, 127, 62],
		[\octaveMin, \int, -5, 5, 0],
		[\octaveMax, \int, -5, 5, 1]]
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
	[[\lengthMin, \int, 0, 24, 2],
		[\lengthMax, \int, 0, 24, 10],
		[\noteMin, \int, 25, 125, 45],
		[\noteMax, \int, 25, 125, 110]]
);

~java.addDefinition("IntScales", \chooser,
	{
		[
			["pentatonic", [0, 3, 5, 7, 10]],
			["Major", [0, 2, 4, 5, 7, 9, 11]],
			["Minor", [0, 2, 3, 5, 10, 11]],
			["Chromatic", [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]],
			["Ones", [0, 1]]
		]
	},
	[]
);

// ~java.newChooser(\scale,
// 	[["pentatonic", [0, 3, 5, 7, 10]],
// 		["minor", [0, 1, 2, 3, 4]],
// 	["major", [0, 5, 6, 9, 10]]]
// );
