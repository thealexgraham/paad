(
/*// make some synthdefs to play with
SynthDef("distortion", { arg inBus, outBus = 0, preGain = 20, gain = 0.5;
    var sig;
    sig = In.ar(inBus, 2);
    sig = (sig * preGain).distort;
    Out.ar(outBus, sig * gain);
}).readyLoad;*/

~java.addDefinition("distortion",
	\effect,
	{ arg inBus, outBus = 0, preGain = 20, gain = 0.5;
		var sig;
		sig = In.ar(inBus, 2);
		sig = (sig * preGain).distort;
		Out.ar(outBus, sig * gain);
	},

	[[\preGain, 0, 1000, 20], [\gain, 0, 1, 0.5]]);

~java.addDefinition("justnoise",
	\effect,
	{ arg inBus, outBus = 0, preGain = 20, gain = 0.5;
		var sig;
		sig = In.ar(inBus, 2);
		sig = SinOsc.ar * 0.2;
		Out.ar(outBus, sig * gain);
	},

	[[\preGain, 0, 1000, 20], [\gain, 0, 1, 0.5]]);


)