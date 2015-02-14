(
var dirname = thisProcess.nowExecutingPath.dirname;
var filename = thisProcess.nowExecutingPath.asString.replace(dirname, "").replace("/","").replace("\\","").replace(".scd","");
filename.postln;

// EQ (courtesy of) Djého Youn & Wouter Snoei
SynthDef(\eq, { |inBus, out =0, lofreq = 80, midfreq = 800, hifreq = 2000, band1 = -45, band2 = -45, band3 = -45, mix = -1|
	var dry, wet;
	dry = In.ar(inBus, 2);

	// wet = BPeakEQ.ar(dry, lofreq, 1, band1);
	// wet = BPeakEQ.ar(wet, midfreq, 1, band2);
	// wet = BPeakEQ.ar(wet, hifreq, 1, band3);

	wet = BLowShelf.ar(dry, lofreq, 1, band1);
	wet = BPeakEQ.ar(wet, midfreq, 1, band2);
	wet = BHiShelf.ar(wet, hifreq, 1, band3);
	wet = Clip.ar(wet, -0.1, 0.1);
	Out.ar(out, XFade2.ar(dry, wet, mix));
}).add;

~java.addDefinition("eq", \effect,
	[[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
[\band3, -80, 80, 0]]);

// Simple reverb
SynthDef(\verb, { |inBus, out=0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0|
	var sig;
	sig = In.ar(inBus, 1);
	sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
	Out.ar(out, sig);
}).add;

~java.addDefinition("verb", \effect,
	[[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
[\damp, 0, 1, 1]]);


// Tape delay (organized like a real one)
SynthDef(\tapedelay, {
	| inBus, out = 0, delaytime = 1, feedback = 0.5, wet = 0.5 |
	var sig, playback, record, return, fb, frames, buffer, phase;

	sig = In.ar(inBus, 1);

	frames = ControlDur.ir * SampleRate.ir;
	buffer = LocalBuf(frames, 1).clear;
	phase = Phasor.ar(0, 1, 0, frames);


	playback = (feedback * DelayN.ar(BufRd.ar(1, buffer, phase), 30, delaytime));

	record = playback + sig;
	BufWr.ar(record, buffer, phase); // Record Signal Input plus Player input
	Out.ar(out, [record, record]);
}).add;

~java.addDefinition("tapedelay", \effect,
	[[\delaytime, 0, 10, 4],
[\feedback, 0, 1, 0.9]]);

)