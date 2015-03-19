~java.addDefinition(\fader, \effect,
	{ | out=0 inBus mute=1 amp=1 |
			Out.ar(out, In.ar(inBus, 2) * mute * amp)
		},
	[
		[\amp, \float, 0.00, 1.00, 0.50],
	]
);
~java.addDefinition(\sawWave, \synth,
	{ | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
		Out.ar(outBus, Pan2.ar(Saw.ar(freq) * gain, pan));
	},
	[
		[\freq, \float, 0.00, 1000.00, 440.00],
		[\gain, \float, 0.00, 1.00, 0.00],
		[\pan, \float, -1.00, 1.00, 0.00],
	]
);
