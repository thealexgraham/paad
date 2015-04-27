~paad.addDefinition(\masterFader, \effect,
	{
		| outBus=0 inBus mute=1 masterGain=1 |
		Out.ar(outBus, In.ar(inBus, 2) * mute * masterGain)
	},
	~paad.createParamsList(
		~paad.addFloat(\masterGain, 0.00, 1.00, 0.50),
	)
);

~paad.addDefinition(\SimpleScale, \patternGen,
	{
		|lengthMin, lengthMax, scale, root, octaveMin, octaveMax|

		var durations = [[1, 1], [0.5, 0.5, 1],
			[0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];
		var finalPattern;

		var length = rrand(lengthMin, lengthMax);
		var notes = Array.fill(length, { |i|
			// Create octave choices
			var octaves = (octaveMin..octaveMax);
			// Pick a scale degree
			var degree = scale.choose;
			// Pick an octave
			var octave = octaves.choose;
			// Add the root to the degree, plus the octave
			(root + degree) + (octave * 12);
		});

		var durationPattern = Array.newFrom(durations.choose);

		// Create a combined note/duration array
		finalPattern = Array.fill(length, { |i|
			var currentDuration;

			// If the duration pattern has run out, pick a new one
			if (durationPattern.size == 0, {
				durationPattern = Array.newFrom(durations.choose);
			});
			// Get the next duration out of the pattern
			currentDuration = durationPattern[0];
			durationPattern.removeAt(0);

			// Add a [note, dur] pair to the array
			[notes[i], currentDuration];
		});

		// Add rests
		finalPattern = finalPattern.add([\rest, 2]);
		// Return the Pattern
		finalPattern;
	},
	~paad.createParamsList(
		~paad.addChoice(\scale, "pentatonic",
			[ 0, 3, 5, 6, 10 ], [ \degrees ]),
		~paad.addInt(\root, 0, 127, 62),
		~paad.addInt(\octaveMin, -3, 3, 0),
		~paad.addInt(\octaveMax, -3, 3, -1),
		~paad.addInt(\lengthMin, 0, 10, 5),
		~paad.addInt(\lengthMax, 0, 10, 9),
		~paad.setReturnType([ [ \midi ], [ \dur ] ])
	)
);
~paad.addDefinition(\ClassicPlayer, \patternPlayer,
	{
		| player, pattern |
		Prout({| ev |
			var pat;
			block { |break|
				loop {
					pat = Pbindf(*[
						// Connected instrument data
						player.template,
						// the current pattern's value
						#[\midinote, \dur], Pseq(pattern.value)
					]);
					// Perform the played action
					player.doPlayedAction;
					// Embed the pattern and wait for it to play
					ev = pat.embedInStream(ev);
				}
			}
		});
	},
	~paad.createParamsList(
		~paad.addChoice(\pattern, "default",
			[], [[\midi], [\dur]]),
	)
);
~paad.addDefinition(\sinInst, \instrument,
	{
		| outBus, freq, gate, amp, pan, gain |
		var sig, env;

		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01),
			gate, doneAction:2);

		sig = SinOsc.ar(freq);
		sig = sig * env * amp;
		Out.ar(outBus, Pan2.ar(sig, pan, gain));
	},
	~paad.createParamsList(
		~paad.addFloat(\gain, 0.00, 1.00, 0.80),
		~paad.addFloat(\pan, -1.00, 1.00, 0.00),
	)
);
~paad.addDefinition(\eq, \effect,
	{
		| inBus, outBus =, lofreq, midfreq,
		hifreq, band1, band2, band3, mix, gain |
		var dry, wet;

		dry = In.ar(inBus, 2);

		wet = BLowShelf.ar(dry, lofreq, 1, band1);
		wet = BPeakEQ.ar(wet, midfreq, 1, band2);
		wet = BHiShelf.ar(wet, hifreq, 1, band3);
		wet = Clip.ar(wet, -0.1, 0.1);

		Out.ar(outBus, XFade2.ar(dry * gain, wet * gain, mix));
	},
	~paad.createParamsList(
		~paad.addFloat(\lofreq, 0.00, 600.00, 80.00),
		~paad.addFloat(\band1, -80.00, 80.00, 0.00),
		~paad.addFloat(\midfreq, 200.00, 1000.00, 800.00),
		~paad.addFloat(\band2, -80.00, 80.00, 0.00),
		~paad.addFloat(\hifreq, 800.00, 5000.00, 2000.00),
		~paad.addFloat(\band3, -80.00, 80.00, 0.00),
		~paad.addFloat(\mix, -1.00, 0.00, 1.00),
	)
);
~paad.addDefinition(\PercentWalk, \changeFunc,
	{
		| value, min, max, walkPercent |

		var changeTop, change;
		changeTop = (max - min) * (walkPercent / 100);
		change = changeTop.rand * [1, -1].choose;
		value + change;
	},
	~paad.createParamsList(
		~paad.addFloat(\walkPercent, 0.00, 100.00, 10.00),
	)
);
~paad.addDefinition(\Default, \changeFunc,
	{
		| default |
		default;
	},
	~paad.createParamsList(
	)
);

~paad.addDefinition(\PercentSwitch, \changeFunc,
	{
		| value, default, min, max, switchPercent |

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
		changeTop = range * (switchPercent / 100);
		change = changeTop.rand * direction;
		default + change;
	},
	~paad.createParamsList(
		~paad.addFloat(\switchPercent, 0.00, 100.00, 10.00),
	)
);
~paad.addDefinition(\verb, \effect,
	{
		| inBus, outBus, mix, room, damp, mul, add, gain |
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
		Out.ar(outBus, sig * gain);
	},
	~paad.createParamsList(
		~paad.addFloat(\verbMix, 0.00, 1.00, 0.20),
		~paad.addFloat(\room, 0.00, 1.00, 1.00),
		~paad.addFloat(\damp, 0.00, 1.00, 1.00),
	)
);
~paad.addDefinition(\feedbackdelay, \effect,
	{ | inBus, outBus, delaytime, feedback, wet = 0.5, gain |
		var laggedTime = Lag.kr(delaytime, 1);
		var sig = SwitchDelay.ar(
			In.ar(inBus, 2),
			delaytime: laggedTime,
			delayfactor: feedback,
			wetlevel: wet
		);
		Out.ar(outBus, sig * gain);
	},
	~paad.createParamsList(
		~paad.addFloat(\delaytime, 0.00, 10.00, 4.00),
		~paad.addFloat(\feedback, 0.00, 1.00, 0.90),
	)
);

~paad.addDefinition(\ScalesInt, \chooser,
	{
		[
			["pentatonic", [0, 3, 5, 7, 10]],
			["major", [0, 2, 4, 5, 7, 9, 11]],
			["minor", [0, 2, 3, 5, 10, 11]],
			["chromatic", [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]],
		]
	},
	~paad.createParamsList(
		~paad.setReturnType([\degrees])
	)
);

~paad.addDefinition(\RandomPass, \taskRunner,
	{
		| object, passPercent |
		var choice = 100.rand;
		if (choice < passPercent.value,
			{ object.sendActionMessage; }
		)
	},
	~paad.createParamsList(
		~paad.addFloat(\passPercent, 0.00, 100.00, 50.00),
	)
);
