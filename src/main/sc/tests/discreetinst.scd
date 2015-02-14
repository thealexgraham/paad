(

~gainControl.free;
~panControl.free;

~gainControl = Bus.control(s);
~panControl = Bus.control(s);

SynthDef(\sawsin, { | out = 0, freq = 0, gate = 1, amp = 1, saw = 1, pan = 0, gain = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = SinOsc.ar(freq);
	sig = sig + (Saw.ar(freq) * saw);
	sig = sig * env * amp;
	Out.ar(out, Pan2.ar(sig, pan, gain));
}).add;


~gainControl.set(1);
~panControl.set(0);
~a1 = [[5, 2], [8, 2], [10, 2], [8, 2], [\rest, 16]];

~patt = Pbind(\instrument, \sawsin,
	\legato, 0.9,
	\gain,3,
	\pan, 4,
	\out, 0);



Array.as
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