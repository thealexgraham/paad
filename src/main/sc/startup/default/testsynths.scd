(

~java.addDefinition(\bufPlayer, \synth,
	{
		| outBus, buff, pan, gain |
		var sig = BufRd.ar(1, buff, Phasor.ar(0, BufRateScale.kr(buff), 0, BufFrames.kr(buff)));
		Out.ar(outBus, Pan2.ar(sig, pan) * gain);
	},
	~java.createParamsList(
		~java.addChoice(\buff, "None", -1, \buffnum)
	)
);

	(thisProcess.nowExecutingPath.dirname ++ "/startup/*").pathMatch.do({
		|item, i|
		postln("Loading " ++ item);
		item.load;
		// ~java.defFolder = "default";
		// (item ++ "*").loadPaths;
	});


~java.addDefinition("SoundsLoad", \chooser,
	{
		var choices = [];
		var directory = "C:/Program Files (x86)/SuperCollider-3.6.6/sounds/";
		var files = [ "C:/Program Files (x86)/SuperCollider-3.6.6/sounds/rhodes.aif",
			"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/synth.wav",
			"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/tom.wav",
			"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/warm.wav" ];

		files.do({|item, i|
			var buffer = Buffer.read(s, item);
			var fileName = PathName.new(item).fileName;
			choices = choices.add([fileName, buffer.bufnum]);
		});

		choices;
	},
	~java.createParamsList(
		~java.setReturnType(\buffnum)
	)
);


~java.addDefinition("PanFM3OP", \synth,
	{ | outBus = 0, car_freq, ratio1, ratio2,index1,index2, pan = 0, gain = 0.3 |
		var fm = SinOsc.ar(car_freq +
			(SinOsc.ar(car_freq*ratio1 +
				(SinOsc.ar(car_freq*ratio2) * index2)) * index1));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(outBus, Pan2.ar(fm * gain, pan));
	},
	[[\ratio1, 0.0, 5.0, 0.2],
		[\ratio2, 0, 5, 1],
		[\index1, 0, 1000, 100],
		[\index2, 0, 1000, 200],
		[\car_freq, 100, 1000, 200],
		[\pan, -1, 1, 0],
		[\gain, 0.0, 1.0, 0]]);

~java.addDefinition(\sineWave, \synth,
	{ | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
		Out.ar(outBus, Pan2.ar(SinOsc.ar(freq) * gain, pan));
	},
	[[\freq, 0, 1000, 440]]);

~java.addDefinition(\sawWave, \synth,
	{ | outBus, freq, pan, gain |
		var sig = Saw.ar(freq) * gain;
		Out.ar(outBus, Pan2.ar(sig, pan));
	},
	[
		[\freq, 0, 1000, 440],
		[\pan, \float, -1.00, 1.00, 0.00],
		[\gain, \float, 0.00, 1.00, 0.00]
	]
);

~java.addDefinition(\sawWaveMod, \synth,
	{ | outBus = 0, freq = 440, speed = 0.5, pan = 0, gain = 0.5 |
		var sig = Saw.ar(freq);
		sig = sig * SinOsc.ar(speed, 0, 0.5, 1);
		Out.ar(outBus, Pan2.ar(sig * gain, pan));
	},
	[
		[\freq, \float, 0.00, 1000.00, 440.00],
		[\speed, \float, 0.00, 5.00, 0.50],
		[\gain, \float, 0.00, 1.00, 0.00],
		[\pan, \float, -1.00, 1.00, 0.00],
	]
);

~java.addDefinition(\sawWaveVib, \synth,
	{ | outBus, freq, vibSpeed, vibAmount, pan, gain |
		var mod = SinOsc.ar(vibSpeed, mul: vibAmount);
		var sig = Saw.ar(freq + mod);
		Out.ar(outBus, Pan2.ar(sig * gain, pan));
	},
	[
		[\freq, \float, 0.00, 1000.00, 440.00],
		[\vibAmount, \float, 0.00, 100.00, 5.00],
		[\vibSpeed, \float, 0.00, 5.00, 0.50],
		[\gain, \float, 0.00, 1.00, 0.00],
		[\pan, \float, -1.00, 1.00, 0.00],
	]
);

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