~java.addDefinition(\PanFM3OP, \synth,
	{ | outBus = 0, car_freq, ratio1, ratio2,index1,index2, pan = 0, gain = 0.3 |
		var fm = SinOsc.ar(car_freq +
			(SinOsc.ar(car_freq*ratio1 +
				(SinOsc.ar(car_freq*ratio2) * index2)) * index1));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(outBus, Pan2.ar(fm * gain, pan));
	},
	[
		[\ratio1, \float, 0.00, 5.00, 0.20],
		[\ratio2, \float, 0.00, 5.00, 1.00],
		[\index1, \float, 0.00, 1000.00, 100.00],
		[\index2, \float, 0.00, 1000.00, 200.00],
		[\car_freq, \float, 100.00, 1000.00, 200.00],
		[\pan, \float, -1.00, 1.00, 0.00],
		[\gain, \float, 0.00, 1.00, 0.00],
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
