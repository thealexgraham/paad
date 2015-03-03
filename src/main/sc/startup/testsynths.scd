(
~java.addDefinition("scale", \synth,
	{ | outBus=0, gain = 0.5 |
		var trig, div, freq, oct, reps, env, osc, flt;
		oct = [0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3];
		trig = Impulse.kr(2);
		env = EnvGen.kr(Env.perc(0.01, 0.1), trig, 0.5);
		freq = (Stepper.kr(trig, 0, 42, 56, 2, 42)).midicps;
		osc = Saw.ar(freq * [1, 1.01], env);
		//flt = RLPF.ar(osc, freq * 2, 0.7);
		Out.ar(outBus, Pan2.ar(osc * gain, 0));

	},
	[]
);

~java.addDefinition("PanFM3OP", \synth,
	{ | outBus = 0, car_freq, mod_ratio1, mod_ratio2, mod_index1, mod_index2, pan = 0, gain = 0.3 |
		var fm = SinOsc.ar(car_freq +
			(SinOsc.ar(car_freq*mod_ratio1 +
				(SinOsc.ar(car_freq*mod_ratio2) * mod_index2)) * mod_index1));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(outBus, Pan2.ar(fm * gain, pan));
	},
	[[\mod_ratio1, 0.0, 5.0, 0.2],
		[\mod_ratio2, 0, 5, 1],
		[\mod_index1, 0, 1000, 100],
		[\mod_index2, 0, 1000, 200],
		[\car_freq, 100, 1000, 200],
		[\pan, -1, 1, 0],
		[\gain, 0.0, 1.0, 0]]);

~java.addDefinition("sinWave", \synth,
	{ | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
		Out.ar(outBus, Pan2.ar(SinOsc.ar(freq) * gain, pan));
	},
	[[\freq, 0, 1000, 440]]);

~java.addDefinition("sawWave", \synth,
	{ | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
		Out.ar(outBus, Pan2.ar(Saw.ar(freq) * gain, pan));
	},
	[[\freq, 0, 1000, 440]]);

~java.addDefinition("sawsinInst", \instrument,
	{ | outBus = 0, freq = 0, gate = 1, amp = 1, saw = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = SinOsc.ar(freq);
		sig = sig + (Saw.ar(freq) * saw);
		sig = sig * env * amp;
		Out.ar(outBus, Pan2.ar(sig, pan, gain));
	},
	[[\saw, 0, 1, 1]]);

~java.addDefinition("sinInst", \instrument,
	{ | outBus = 0, freq = 0, gate = 1, amp = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = SinOsc.ar(freq);
		sig = sig * env * amp;
		Out.ar(outBus, Pan2.ar(sig, pan, gain));
	},
	[]);

)