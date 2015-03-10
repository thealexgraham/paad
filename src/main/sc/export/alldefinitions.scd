~java.addDefinition(\sineWave, \synth,
	{ | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
		Out.ar(outBus, Pan2.ar(SinOsc.ar(freq) * gain, pan));
	},
	[
		[\freq, \float, 0.00, 1000.00, 440.00],
		[\gain, \float, 0.00, 1.00, 0.00],
		[\pan, \float, -1.00, 1.00, 0.00],
	]
);
~java.addDefinition(\verb, \effect,
	{ |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
		Out.ar(outBus, sig * gain);
	},
	[
		[\verbMix, \float, 0.00, 1.00, 0.20],
		[\room, \float, 0.00, 1.00, 1.00],
		[\damp, \float, 0.00, 1.00, 1.00],
	]
);
~java.addDefinition(\scale, \synth,
	{ | outBus=0, speed = 2, gain = 0.5, pan = 0 |
		var trig, div, freq, oct, reps, env, osc, flt;
		oct = [0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3];
		trig = Impulse.kr(speed);
		env = EnvGen.kr(Env.perc(0.01, 0.1), trig, 0.5);
		freq = (Stepper.kr(trig, 0, 42, 56, 2, 42)).midicps;
		osc = Saw.ar(freq * [1, 1.01], env);
		//flt = RLPF.ar(osc, freq * 2, 0.7);
		Out.ar(outBus, Pan2.ar(osc * gain, pan));

	},
	[
		[\speed, \float, 0.00, 5.00, 2.00],
		[\gain, \float, 0.00, 1.00, 0.00],
		[\pan, \float, -1.00, 1.00, 0.00],
	]
);
~java.addDefinition(\verb, \effect,
	{ |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
		Out.ar(outBus, sig * gain);
	},
	[
		[\verbMix, \float, 0.00, 1.00, 0.20],
		[\room, \float, 0.00, 1.00, 1.00],
		[\damp, \float, 0.00, 1.00, 1.00],
	]
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
	[
		[\delaytime, \float, 0.00, 10.00, 4.00],
		[\feedback, \float, 0.00, 1.00, 0.90],
	]
);
