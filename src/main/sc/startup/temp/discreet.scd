// Patterns

~java.addDefinition("SimpleScale", \patternGen,
	{
		|lengthMin, lengthMax, scale, root, octaveMin, octaveMax|

		var durations = [[1, 1], [0.25, 0.25, 0.25, 0.25], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [0.25, 0.25, 0.5]];

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

		finalPattern = finalPattern.add([\rest, 0]);
		finalPattern;
	},

	~java.createParamsList(
		~java.addChoice(\scale, "pentatonic", [0,3,5,6,10], [\degrees]),
		~java.addInt(\root, 0, 127, 62),
		~java.addRange(\octave, \int, -5, 5, 0, -1, 1),
		~java.addRange(\length, \int, 0, 20, 5, 9),
		~java.setReturnType( [[\midi],[\dur]] );
	),

);

// Scales

~java.addDefinition("ScalesInt", \chooser,
	{
		[
			["pentatonic", [0, 3, 5, 7, 10]],
			["major", [0, 2, 4, 5, 7, 9, 11]],
			["minor", [0, 2, 3, 5, 10, 11]],
			["chromatic", [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]],
		]
	},
	~java.createParamsList(
		~java.setReturnType( [\degrees] );
	)
);

// Instruments

~java.addDefinition(\sinInst, \instrument,
	{ | outBus = 0, freq = 0, gate = 1, amp = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = SinOsc.ar(freq);
		sig = sig * env * amp;
		Out.ar(outBus, Pan2.ar(sig, pan, gain));
	},
	~java.createParamsList(
		~java.addFloat(\gain, 0.00, 1.00, 0.00),
		~java.addFloat(\pan, -1.00, 1.00, 0.00),
	)
);

~java.addDefinition(\sawInst, \instrument,
	{ | outBus = 0, freq = 0, gate = 1, amp = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = Saw.ar(freq);
		sig = sig * env * amp;
		Out.ar(outBus, Pan2.ar(sig, pan, gain));
	},
	~java.createParamsList(
		~java.addFloat(\gain, 0.00, 1.00, 0.00),
		~java.addFloat(\pan, -1.00, 1.00, 0.00),
	)
);

// Effects

~java.addDefinition(\eq, \effect,
	{ |inBus, outBus =0, lofreq = 80, midfreq = 800, hifreq = 2000, band1 = -45, band2 = -45, band3 = -45, mix = -1, gain = 1|
		var dry, wet;
		dry = In.ar(inBus, 2);

		// wet = BPeakEQ.ar(dry, lofreq, 1, band1);
		// wet = BPeakEQ.ar(wet, midfreq, 1, band2);
		// wet = BPeakEQ.ar(wet, hifreq, 1, band3);

		wet = BLowShelf.ar(dry, lofreq, 1, band1);
		wet = BPeakEQ.ar(wet, midfreq, 1, band2);
		wet = BHiShelf.ar(wet, hifreq, 1, band3);
		// wet = Clip.ar(wet, 0.5, 0.7);

   wet =  Compander.ar(wet, wet,
        thresh: 1.0,
        slopeBelow: 1,
        slopeAbove: 0.5,
        clampTime: 0.01,
        relaxTime: 0.01
    );

		Out.ar(outBus, XFade2.ar(dry * gain, wet * gain, mix));
	},
	~java.createParamsList(
		~java.addFloat(\lofreq, 0.00, 600.00, 80.00),
		~java.addFloat(\band1, -80.00, 80.00, 0.00),
		~java.addFloat(\midfreq, 200.00, 1000.00, 800.00),
		~java.addFloat(\band2, -80.00, 80.00, 0.00),
		~java.addFloat(\hifreq, 800.00, 5000.00, 2000.00),
		~java.addFloat(\band3, -80.00, 80.00, 0.00),
		~java.addFloat(\mix, -1.00, 0.00, 1.00),
	)
);

~java.addDefinition(\feedbackdelay, \effect,
	{ | inBus = 0, outBus = 0, delaytime = 1, feedback = 0.5, wet = 0.5, gain = 1 |
		var laggedTime = Lag.kr(delaytime, 1);
		var sig = SwitchDelay.ar(
			In.ar(inBus, 2),
			delaytime: laggedTime,
			delayfactor: feedback,
			wetlevel: wet
		);
		Out.ar(outBus, sig * gain);
	},
	~java.createParamsList(
		~java.addFloat(\delaytime, 0.00, 10.00, 4.00),
		~java.addFloat(\feedback, 0.00, 1.00, 0.90),
	)
);

~java.addDefinition(\verb, \effect,
	{ |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp, mul, add) + sig;
		Out.ar(outBus, sig * gain);
	},
	~java.createParamsList(
		~java.addFloat(\mix, 0.00, 1.00, 1.00),
		~java.addFloat(\room, 0.00, 1.00, 1.00),
		~java.addFloat(\damp, 0.00, 1.00, 1.00),
	)
);

// Pattern Player

~java.addDefinition("ClassicPlayer",
	\patternPlayer,
	{
		| player, pattern |

		Prout({| ev |
			var pat;
			block { |break|
				loop {
					"Playing pattern".postln;
					pat = Pbindf(*[
						player.template, // Connected instrument data
						#[\midinote, \dur], Pseq(pattern.value) // the current pattern's value
					]);
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