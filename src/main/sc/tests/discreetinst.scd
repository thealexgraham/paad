(

SynthDef(\sawsin, { | outBus = 0, freq = 0, gate = 1, amp = 1, saw = 1, pan = 0, gain = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = SinOsc.ar(freq);
	sig = sig + (Saw.ar(freq) * saw);
	sig = sig * env * amp;
	Out.ar(outBus, Pan2.ar(sig, pan, gain));
}).add;

// Simple reverb
SynthDef(\verb, { |inBus, outBus = 0, mix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0, gain = 1|
	var sig;
	sig = In.ar(inBus, 2);
	sig = FreeVerb.ar(sig, mix, room, damp, mul, add);
	Out.ar(outBus, sig * gain);
}).add;

~verbInBus.free;
~verbInBus = Bus.audio(s, 2);
~verb = Synth.tail(s, \verb, [\inBus, ~verbInBus, \outBus, 0, \mix, 0.5, \room, 1, \damp, 1]);


~a1 = [[5, 2], [6, 2], [1, 2], [8, 2]];


~outBusControl.free;
~outBusControl = Bus.control(s);

~outBusControl.value = 0;

~patt = Pbind(\instrument, \sawsin,
	\legato, 0.9,
	\gain,3,
	\pan, 4,
	\outBus, ~outBusControl.asMap);


#[\instrument, \sawsin, \legato, 0.9, \gain,3, \pan, 2, \out, 0];

//~rests = Pbindf(~patt, #[\note, \dur], [[\rest, 4]]);

f = Pseq([
	Pbindf(~patt,
		//\amp, 0.2,
		#[\note, \dur], Pseq(~a1, 5),
		\octave, 4,
)]).play;

)

(

~outBusControl.set(~verbInBus.index);
~outBusControl.get({|v| ("outBusControl value "++v).postln});
)

(
a = Pbind([\instrument, \legato], [\sawsin, 0.3, 0]);
x = a.asStream;
)
(
a = Pbind(\instrument, \sawsin,
	\legato, 0.9,
	\gain,3,
	\pan, 4,
	\out, 0);
x = a.asStream;
)

(
a = Pbind(\x, Pseq([1, 2, 3]), \y, Prand([100, 300, 200], inf), \zzz, 99);
x = a.asStream;
)

x.next(()); // pass in an event ()
x.next(());
x.next(());
x.next(()); // end: nil