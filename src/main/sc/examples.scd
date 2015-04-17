// Simple Reverb
~java.addDefinition(\verb, \effect,
	{ |inBus, outBus, mix, room, damp, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = FreeVerb.ar(sig, mix, room, damp);
		Out.ar(outBus, sig * gain);
	},
	[[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
		[\damp, 0, 1, 1]]
);
