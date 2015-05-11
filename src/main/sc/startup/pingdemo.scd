(

~java.addDefinition(\sinBlip, \synth,
	{ | outBus, speed, freq, pan, gain |
		var sig, trig, env;
		sig = SinOsc.ar(freq) * gain;
		trig = Impulse.kr(speed);
		env = EnvGen.kr(Env.perc(0.01, 0.08), trig, 0.5);
		Out.ar(outBus, Pan2.ar(sig * env, pan));
	},
	~java.createParamsList(
		~java.addFloat(\speed, 0.00, 5.0, 1.00),
		~java.addFloat(\freq, 0.00, 1000.00, 440.00),
		~java.addFloat(\pan, -1.00, 1.00, 0.00),
		~java.addFloat(\gain, 0.00, 1.00, 0.00),
	)
);

~java.addDefinition(\sinPing, \synth,
	{ | outBus, freq, pan, gain |
		var env = EnvGen.ar(Env.perc);
		Out.ar(outBus, Pan2.ar(SinOsc.ar(freq), pan, gain) * env);
    },
	~java.createParamsList(
		~java.addFloat(\freq, 60, 600, 400)
	)
);

~java.addDefinition(\sawPing, \synth,
	{ | outBus, freq, pan, gain |
		var env = EnvGen.ar(Env.new([1,0],[0.2]));
		Out.ar(outBus, Pan2.ar(RLPF.ar(Saw.ar(freq), freq * 6, 0.1), pan, gain) * env);
    },
	~java.createParamsList(
		~java.addFloat(\freq, 60, 600, 400)
	)
);

~java.addDefinition(\noisePing, \synth,
	{ | outBus, pan, freq, gain |
		var env = EnvGen.ar(Env.new([1,0],[0.2]));
		Out.ar(outBus, Pan2.ar(Resonz.ar(GrayNoise.ar, freq * 2, 0.1), pan, gain * 2) * env);
    },
	~java.createParamsList(
		~java.addFloat(\freq, 60, 600, 400)
	)
);

~java.addDefinition(\pingComb, \effect,
	{ |inBus, outBus|
		var in;
		in = In.ar(inBus, 2);
		Out.ar(outBus, CombN.ar(in, 0.2, 0.2, 3, 1, in));
	}
);


~java.addDefinition("ExpRandRange",
	\changeFunc,
	{
		| value, min, max, percentMin, percentMax |
		exprand((min * (percentMin / 100)), (max * (percentMax / 100)));
	},
	~java.createParamsList(
		~java.addRange(\percent, \float, 0, 100, 0, 100);
	)
);

~java.addDefinition("Exprand",
	\changeFunc,
	{
		| value, min, max, percentMin, percentMax |
		exprand(min, max);
	}
);

~java.addDefinition("RandomPass",
	\taskRunner,
	{
		| object, percent |
		var choice = 100.rand;
		if (choice < percent.value,
			{ object.sendActionMessage; }
		)
	},
	~java.createParamsList(
		~java.addFloat(\percent, 0, 100, 50);
	)
);


~java.addDefinition("RandomDelay",
	\taskRunner,
	{
		| object, waitMin, waitMax |
		var time = rrand(waitMin.value * 1.0, waitMax.value);
		time.wait;
		object.sendActionMessage;
	},
	~java.createParamsList(
		~java.addRange(\wait, \float, 0, 5, 1, 5);
	)
);

)