(
~java.defFolder = thisProcess.nowExecutingPath.split.last;

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
		// wet = Clip.ar(wet, -0.1, 0.1);


		wet = Compander.ar(wet, wet,
			thresh:1,
			slopeBelow: 1,
			slopeAbove: 0.1,
			clampTime: 0.01,
			relaxTime: 0.01
		);


		Out.ar(outBus, XFade2.ar(dry * gain, wet * gain, mix));
	},
	[[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
		[\band3, -80, 80, 0],
		[\mix, -1, 0, 1]]
);

// Simple Reverb
~java.addDefinition(\verb, \effect,
	{ |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
		Out.ar(outBus, sig * gain);
	},
	[[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
		[\damp, 0, 1, 1]]
);

~java.addDefinition(\feedbackdelay, \effect,
	{ | inBus = 0, outBus = 0, delaytime = 1, feedback = 0.5, wet = 0.5, gain = 1 |
		Out.ar(outBus,
			SwitchDelay.ar(
				In.ar(inBus, 2),
				delaytime: delaytime,
				delayfactor: feedback,
				wetlevel: wet
		) * gain);
	},
	[[\delaytime, 0, 10, 4],
		[\feedback, 0, 1, 0.9]]
);

~java.addDefinition(\combDelay, \effect,
	{ | inBus = 0, outBus = 0, delaytime = 4, decaytime = 4 |
		Out.ar(outBus,
			CombC.ar(In.ar(inBus, 2), 10, delaytime, decaytime))
	},
	[[\delayTime, 0, 10, 4],
		[\decayTime, 0, 4, 10]]
);

)