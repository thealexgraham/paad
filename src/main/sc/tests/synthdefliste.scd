s.dumpOSC(1);

(
// make some synthdefs to play with
SynthDef("distortion", { arg inBus, outBus = 0, preGain = 20, gain = 0.5;
    var sig;
    sig = In.ar(inBus, 2);
    sig = (sig * preGain).distort;
    Out.ar(outBus, sig * gain);
}).readyLoad);
)

(
SynthDef.synthDefDir
)

(
d = SynthDef(\demoWrapping, { |out|
    Out.ar(out, SynthDef.wrap({ |freq| SinOsc.ar(freq) }))
});

d.allControlNames;
)

(
// make some synthdefs to play with
SynthDef("other", { arg inBus, outBus = 0, preGain = 20, gain = 0.5;
    var sig;
    sig = In.ar(inBus, 2);
    sig = (sig * preGain).distort;
    Out.ar(outBus, sig * gain);
});
)

(
OSCdef('dump', { |msg|
	msg.postln;
}, '/*');
)

(
	// This function will run when the Java app is ready and listening
OSCdef(\def_done, { |msg|
	msg.postln;
}, '/done');
)

(
	// This function will run when the Java app is ready and listening
OSCdef(\def_done2, { |msg|
	msg.postln;
}, '/done2');
)


(
	// This function will run when the Java app is ready and listening
OSCdef(\def_sent, { |msg|
	"Def wsent".postln;
}, '/d_recv');
)


(
o = OSCresponderNode(s.addr, '/done', { |time, resp, msg| msg.postln }).add;
)

(
o = OSCresponderNode(s.addr, '/tr', { |time, resp, msg| msg.postln }).add;

{	var	freq;
	freq = LFNoise1.kr(2, 600, 800);
		// Impulse is needed to trigger the /tr message to be sent
	SendTrig.kr(Impulse.kr(4), 0, freq);
	SinOsc.ar(freq, 0, 0.3) ! 2
}.play;

)