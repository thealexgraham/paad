~java.newPatternGen("SimpleScale", {
	|lengthMin, lengthMax, scale, root, octaveMin, octaveMax|

	var durations = [[1, 1], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];

	var finalPattern;

	var length = rrand(lengthRange[0], lengthRange[1]);
	var notes = Array.fill(length, { |i|
		var octaves = (octaveRange[0]..octaveRange[1]);
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

	~pattern = finalPattern.add([\rest, 8]);
}, [[\lengthMin, \int, 0, 10, 4],
		[\lengthMax, \int, 0, 16, 8],
		[\scale, \choice, ["pentatonic", [0, 3, 5, 7, 10]]], //??
		[\root, \int, 0, 127, 62],
		[\octaveMin, \int, -5, 5, 0],
		[\octaveMax, \int, -5, 5, 1]]
);

~java.newChooser(\scale,
	[["pentatonic", [0, 3, 5, 7, 10]],
		["minor", [0, 1, 2, 3, 4]],
		["major", [0, 5, 6, 9, 10]]]
);
