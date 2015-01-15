(
var java;

s = Server(\javaSCServer, NetAddr("127.0.0.1", 57320));
Server.default = s;

s.waitForBoot({
	"server ready".postln;

	//("receivePort:"++NetAddr.langPort).postln;
	javaCommand("receivePort", NetAddr.langPort);

	Routine { arg inval;
		loop {
			// thisThread refers to the routine.
			/*postf("|\n");
			postf("avgCPU:%\n", s.avgCPU);
			postf("peakCPU:%\n", s.peakCPU);*/
			javaCommand("peakCPU", s.peakCPU);
			javaCommand("avgCPU", s.avgCPU);
			0.5.yield;
			nil;
		}
	}.play;

	n = NetAddr("127.0.0.1", NetAddr.langPort);

	r = OSCresponder(nil, '/testing', { arg time, resp, msg;
		msg.postln;
	}).add;

	SynthDef(\PanFM3OP, { | car_freq, mod_ratio1, mod_ratio2, mod_index1, mod_index2, pan = 0, amp = 0.3 |
		var fm = SinOsc.ar(car_freq +
			(SinOsc.ar(car_freq*mod_ratio1 +
		(SinOsc.ar(car_freq*mod_ratio2) * mod_index2)) * mod_index1));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(0, Pan2.ar(fm * amp, pan));
	}).add;

	SynthDef(\sinWave, { | pan = 0, amp = 0.5 |
		Out.ar(0, Pan2.ar(SinOsc.ar(220) * amp, pan));
	}).add;

	OSCFunc( { |msg|
		"Creating Synths".postln;
		// [name, min, max, default]
		~params = [[\mod_ratio1, 0.0, 5.0, 0.2],
			[\mod_ratio2, 0, 5, 1],
			[\mod_index1, 0, 1000, 100],
			[\mod_index2, 0, 1000, 200],
			[\car_freq, 100, 1000, 200],
			[\pan, -1, 1, 1],
		[\amp, 0.0, 1.0, 0]];

		java.sendSynth("PanFM3OP", ~params);
		java.startSynth("PanFM3OP", ~params);

		java.newSynth("sinWave", []);

	}, '/start/ready', recvPort:NetAddr.langPort);

	/*
	OSCresponder(nil, '/start/ready', { arg time, resp, msg;
	nil
	});*/

	java = JavaHelper.new(1250);

	"ready".postln;
}, 100);

)
	