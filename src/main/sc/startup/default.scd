~java.addDefinition("scale", \synth,
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
	[[\speed, 0, 5, 2]]
);

~java.addDefinition(\gverb, \effect,
	{ |inBus, outBus = 0,roomsize = 150, revtime = 2,
		damping = 0.5, inputbw = 0.34,
		spread = 15, drylevel = 0.7, earlylevel = 0.7, taillevel = 0.5, gain = 1|
		var sig;
		sig = In.ar(inBus, 2);
		sig = GVerb.ar(
			        sig,
			        roomsize,
			        revtime,
			        damping,
			        inputbw,
			        spread,
			        drylevel,
			        earlylevel,
			        taillevel, 500);
		Out.ar(outBus, sig * gain);
	},
	~java.createParamsList(
		~java.addFloat(\revtime, 0.00, 5.0, 2.0),
		~java.addFloat(\roomsize, 0.00, 500, 243),
		~java.addFloat(\devtime, 0.00, 1.00, 0.34),
	)
);

~java.addDefinition("jumpEffect", \effect,
	// this version has no lag so delay time jumps around with clicks
	{
		|inBus, outBus|
		var input, delayTime, outDelay;
		// input from sound card
		input = In.ar(inBus, 2);
		// delay time jumps every 2 seconds
		delayTime = Demand.kr(Impulse.kr(1/2), 0, Dseq([0.4, 0.6, 0.9, 0.3, 0.7], inf));
		// CombC - delay with feedback
		Out.ar(outBus, CombC.ar(input, 2, delayTime, 6) + input);
	}
);

~java.addDefinition("jumpEffectLag", \effect,
	// this version has no lag so delay time jumps around with clicks
	{
		|inBus, outBus, delayTime|
        var input, laggedDelayTime, outDelay;
        // input from sound card
		input = In.ar(inBus, 2);
	// delay time jumps every 4 seconds
		// delayTime = Demand.kr(Impulse.kr(1/4), 0, Dseq([0.4, 0.6, 0.9, 0.3, 0.7], inf));
        // lagged delay time - takes 3 seconds to reach new value
        laggedDelayTime = Lag.kr(delayTime, 3);
        // CombC - delay with feedback
		Out.ar(outBus, CombC.ar(input, 2, laggedDelayTime, 6) + input);
	},
	~java.createParamsList(
		~java.addFloat(\delayTime, 0.01, 2, 0.5),
	)
);

~java.addDefinition("FM2OP", \synth,
	{ | outBus = 0, freq, ratio, index, pan = 0, gain = 0.3 |
		var fm = SinOsc.ar(freq +
			(SinOsc.ar(freq*ratio) * index));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(outBus, Pan2.ar(fm * gain, pan));
	},
	[[\ratio, 0.0, 5.0, 0.2],
		[\index, 0, 1000, 100],
		[\freq, 100, 1000, 200],
		[\pan, -1, 1, 0],
		[\gain, 0.0, 1.0, 0]]
);

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

~java.addDefinition(\bufPlayer, \synth,
	{
		| outBus, buff, pan, gain |
		var sig = BufRd.ar(1, buff,
			Phasor.ar(0, BufRateScale.kr(buff), 0, BufFrames.kr(buff)));
		Out.ar(outBus, Pan2.ar(sig, pan) * gain);
	},
	~java.createParamsList(
		~java.addChoice(\buff, "None", -1, \buffnum)
	)
);


~java.addDefinition("SoundsLoad", \chooser,
	{
		var choices = [], dir, path;
		dir = "C:/supercollider/sounds/";
		path = PathName.new(dir);

		// Go through each file in the directory
		path.filesDo({ |aFile|
			// Load the file into a buffer
			var buffer = Buffer.read(s, aFile);
			//Store the filename and a reference to
			// the buffer in the choices array
			choices = choices.add([aFile.fileName, buffer.bufnum]);
		});
		// Return the choices array
		choices;
	},
	~java.createParamsList(
		~java.setReturnType(\buffnum)
	)
);


