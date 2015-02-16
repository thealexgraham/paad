(
var dirname = thisProcess.nowExecutingPath.dirname;
var filename = thisProcess.nowExecutingPath.asString.replace(dirname, "").replace("/","").replace("\\","").replace(".scd","");
filename.postln;

// EQ (courtesy of) Djého Youn & Wouter Snoei
SynthDef(\eq, { |inBus, outBus =0, lofreq = 80, midfreq = 800, hifreq = 2000, band1 = -45, band2 = -45, band3 = -45, mix = -1, gain = 1|
	var dry, wet;
	dry = In.ar(inBus, 2);

	// wet = BPeakEQ.ar(dry, lofreq, 1, band1);
	// wet = BPeakEQ.ar(wet, midfreq, 1, band2);
	// wet = BPeakEQ.ar(wet, hifreq, 1, band3);

	wet = BLowShelf.ar(dry, lofreq, 1, band1);
	wet = BPeakEQ.ar(wet, midfreq, 1, band2);
	wet = BHiShelf.ar(wet, hifreq, 1, band3);
	wet = Clip.ar(wet, -0.1, 0.1);
	Out.ar(outBus, XFade2.ar(dry * gain, wet * gain, mix));
}).add;

~java.addDefinition("eq", \effect,
	[[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
		[\band3, -80, 80, 0]]);

// Simple reverb
SynthDef(\verb, { |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
	var sig;
	sig = In.ar(inBus, 2);
	sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
	Out.ar(outBus, sig * gain);
}).add;

~java.addDefinition("verb", \effect,
	[[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
		[\damp, 0, 1, 1]]);


// Tape delay (organized like a real one) (this is broken.)
SynthDef(\tapedelay, {
	| inBus, outBus = 0, delaytime = 1, feedback = 0.5, wet = 0.5, gain = 1 |
	var sig, playback, record, return, fb, frames, buffer, phase;

	sig = In.ar(inBus, 2);

	frames = ControlDur.ir * SampleRate.ir;
	buffer = LocalBuf(frames, 2).clear;
	phase = Phasor.ar(0, 1, 0, frames);


	playback = (feedback * DelayN.ar(BufRd.ar(2, buffer, phase), 30, delaytime));

	record = playback + sig;
	BufWr.ar(record, buffer, phase); // Record Signal Input plus Player input
	Out.ar(outBus, record * gain);
}).add;

// simple feedback delay
SynthDef(\feedbackdelay, {
	| inBus = 0, outBus = 0, delaytime = 1, feedback = 0.5, wet = 0.5, gain = 1 |
	Out.ar(outBus,
		SwitchDelay.ar(
			In.ar(inBus, 2),
			delaytime: delaytime,
			delayfactor: feedback,
			wetlevel: wet
	) * gain);
}).add;

~java.addDefinition("feedbackdelay", \effect,
	[[\delaytime, 0, 10, 4],
		[\feedback, 0, 1, 0.9]]);

)