
~createTempo = {
	// Is this its own object or a global?
	~tc = TempoClock.default;
	~tc.tempo = 100 / 60;
}


(
~a1 = [[5, 5], [8, 1], [10, 1], [8, 5]];

~patt = Pbind(\instrument, \default,
	\legato, 0.9,
	\out, 0);

~rests = Pbindf(~patt, #[\note, \dur], [[\rest, 4]]);

Pseq([
	Pbindf(~patt,
		\amp, 0.2,
		#[\note, \dur], Pseq(~a1, 1),
		\octave, 4,
	)
]).play
PPar

)

