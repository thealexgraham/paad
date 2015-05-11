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

~java.addDefinition("PanLag", \effect,
	{
		|inBus, outBus, pan|
		var leftChan, rightChan, mono, sig;
		sig = In.ar(inBus, 2);
		leftChan = sig[0];
		rightChan = sig[1];

		mono = leftChan + rightChan;

		Out.ar(outBus, Pan2.ar(mono, Lag.kr(pan, 3)));
	},
	~java.createParamsList(
		~java.addFloat(\pan, -1.0, 1.0, 0.0),
		~java.addFloat(\lagTime, 0.01, 5.0, 1.0);
	)
);

~java.addDefinition("PanLFO", \effect,
	{
		|inBus, outBus, speed, gain, gainLag|
		var mono, sig, pan;
		sig = In.ar(inBus, 2);
		mono = sig[0] + sig[1];

		pan = SinOsc.ar(speed);

		Out.ar(outBus,  Lag.kr(gain, gainLag) * Pan2.ar(mono, pan));
	},
	~java.createParamsList(
		~java.addFloat(\pan, -1.0, 1.0, 0.0),
		~java.addFloat(\speed, 0.01, 1.0, 1.0),
		~java.addFloat(\gain, 0.0, 1.0, 0.5),
		~java.addFloat(\gainLag, 0.01, 5.0, 1.0)
	)
);


~java.addDefinition("BuffSelect", \patternGen,
	{
		| bufNums |
		bufNums.choose;
	},

	~java.createParamsList(
		~java.addChoice(\bufNums, "none", [-1], [\buffnums]),
		~java.setReturnType( \buffnum )
	),

);

~java.addDefinition("MultiLoad", \chooser,
	{
		var choices = [], dir, path;
		dir = "C:/supercollider/sounds/";
		path = PathName.new(dir);

		// Go through each file in the directory
		path.folders.do({ |aFolder|
			var buffers = [];
			aFolder.filesDo({ |aFile|
				// Load the file into a buffer
				var buffer = Buffer.read(s, aFile.fullPath);
				buffers = buffers.add(buffer.bufnum);
			});

			//Store the filename and a reference to
			// the buffer in the choices array
			choices = choices.add([aFolder.folderName, buffers]);
		});
		// Return the choices array
		choices;
	},
	~java.createParamsList(
		~java.setReturnType([\buffnums])
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
			var buffer = Buffer.read(s, aFile.fullPath);
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




