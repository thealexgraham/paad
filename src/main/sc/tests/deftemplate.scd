~java.addDefinition(\nameHere, \synth,
	{ | outBus = 0, pan = 0, gain = 0.3 |
		var sig;
		Out.ar(outBus, Pan2.ar(sig * gain, pan));
	},
	[
		//[\paramName, min, max, default],
		[\pan, -1, 1, 0],
		[\gain, 0.0, 1.0, 0]
	]
);

~java.addDefinition(\name, \effect,
	{ | outBus = 0, inBus = 0 |
		var sig;
		sig = In.ar(inBus, 2);

		Out.ar(outBus, sig * gain);
	},

	[
		//[\paramName, min, max, default],
	]
);

~java.addDefinition(\name, \changeFunc,
	{
		| value, min, max |
		rrand(min, max);
	},
	[
		//[\paramName, min, max, default],
	]
);