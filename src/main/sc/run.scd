(
var java;
o = ServerOptions.new;
//o.device = "S/PDIF (6- M-Audio FastTrack Pr";

s = Server(\javaSCServer, NetAddr("127.0.0.1", 57320), o);
Server.default = s;
"my file is running".postln;

s.waitForBoot({
	"server ready".postln;


	NetAddr.langPort.postln;
	javaCommand("receivePort", NetAddr.langPort); // Send java the port sclang is listening on

	// Update the CPU usage
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

	// Synthdefs
	SynthDef(\PanFM3OP, { | car_freq, mod_ratio1, mod_ratio2, mod_index1, mod_index2, pan = 0, gain = 0.3 |
		var fm = SinOsc.ar(car_freq +
			(SinOsc.ar(car_freq*mod_ratio1 +
		(SinOsc.ar(car_freq*mod_ratio2) * mod_index2)) * mod_index1));
		//var env = EnvGen.kr(Env.linen, doneAction: 2);
		Out.ar(0, Pan2.ar(fm * gain, pan));
	}).add;

	SynthDef(\sinWave, { | freq = 440, pan = 0, gain = 0.5 |
		Out.ar(0, Pan2.ar(SinOsc.ar(freq) * gain, pan));
	}).add;

	SynthDef(\sawWave, { | freq = 440, pan = 0, gain = 0.5 |
		Out.ar(0, Pan2.ar(Saw.ar(freq) * gain, pan));
	}).add;

	SynthDef(\sawsin, { | out = 0, freq = 0, gate = 1, amp = 1, saw = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = SinOsc.ar(freq);
		sig = sig + (Saw.ar(freq) * saw);
		sig = sig * env * amp;
		Out.ar(out, Pan2.ar(sig, pan, gain));
	}).add;

	// This function will run when the Java app is ready and listening
	OSCFunc( { |msg|

		"Creating Synths".postln;
		// [name, min, max, default]
		~params = [[\mod_ratio1, 0.0, 5.0, 0.2],
			[\mod_ratio2, 0, 5, 1],
			[\mod_index1, 0, 1000, 100],
			[\mod_index2, 0, 1000, 200],
			[\car_freq, 100, 1000, 200],
			[\pan, -1, 1, 0],
			[\gain, 0.0, 1.0, 0]];

		java.newSynth("PanFM3OP", ~params);
		java.newSynth("sinWave", [[\freq, 0, 1000, 440]]);
		java.newSynth("sawWave", [[\freq, 0, 1000, 440]]);

		java.newInstrument("sawsin", []);
	}, '/start/ready', recvPort:NetAddr.langPort);

	// Create a new java helper at the default port
	java = JavaHelper.new(1250);

	"ready".postln;
}, 100);

)
	