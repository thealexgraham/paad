
(

~rest = { |number| Pbind(\instrument, \default, \note, Pseq([\rest],1), \dur, number); };

// Is this its own object or a global?
~tc = TempoClock.default;
~tc.tempo = 100 / 60;

// Create effects bus
~effectsBus = Bus.audio(s, 1);

// Create the synthesizer
Synth(\discreet, [\in, ~effectsBus, \out, 0]); // EQualizer

// Add the parameters
~discreetParams = [
	["EQ",
		[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
		[\band3, -80, 80, 0],
		[\eqMix, -1, 0, 1],
	],
	["Reverb",
		[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
		[\damp, 0, 1, 1]
	]
	["Delay",
		[\delaytime, 0, 10, ~tc.beatDur * 8 * 0.9],
		[\feedback, 0, 1, 0.9]
	]
];
)

(
~discreetParams = [
	["EQ",
		[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
		[\band3, -80, 80, 0],
		[\eqMix, -1, 0, 1],
	],
	["Reverb",
		[\verbMix,  0, 1, 0.2],
		[\room, 0, 1, 1],
		[\damp, 0, 1, 1],
	],
	["Delay",
		[\delaytime, 0, 10, ~tc.beatDur * 8 * 0.9],
		[\feedback, 0, 1, 0.9],
	]
];
)

//Patterns
(
~a1 = [[5, 1], [8, 1], [10, 1], [8, 5], [\rest, 16]];
~a2 = [[3, 1], [5, 7], [\rest, 0]];
~a3 = [[3, 8], [\rest, 0]];
~a4 = [[\rest, 3], [5, 5], [\rest, 0]];

)

// Control
(
~patt = Pbind(\instrument, \default,
	\legato, 0.9,
	\out, ~effectsBus);

~rests = Pbindf(~patt, #[\note, \dur], [[\rest, 4]]);

Pseq([
	Pbindf(~patt,
		\amp, 0.2,
		#[\note, \dur], Pseq(~a1, 1),
		\octave, 4,
	),
	//~rest.value(8),
	Pbindf(~patt,
		\amp, 0.6,
		#[\note, \dur], Pseq(~a2, 1),
		\octave, 4
	),
	//~rest.value(4),
	Pbindf(~patt,
		\amp, 0.4,
		#[\note, \dur], Pseq(~a3, 1),
		\octave, 4
	),
	//~rest.value(2),
	Pbindf(~patt,
		\amp, 0.4,
		#[\note, \dur], Pseq(~a4, 1),
		\octave, 4
	)
]).play
)

//Synths
(
SynthDef(\discreet, { |in, out =0,
	lofreq = 80, midfreq = 800,
	hifreq = 2000, band1 = 5,
	band2 = -45, band3 = 12, eqMix = 1,
	verbMix = 0.33, room = 0.5, damp = 0.5, mul = 1, add = 0,
	delaytime = 1, feedback = 0.5 |

	var dry, wet, mixed; // EQ
	var sig, flt, playback, record, return, fb, frames, buffer, phase; // Tape Delay

	// EQ //
	dry = In.ar(in, 1);

	wet = BLowShelf.ar(dry, lofreq, 1, band1);
	wet = BPeakEQ.ar(wet, midfreq, 1, band2);
	wet = BHiShelf.ar(wet, hifreq, 1, band3);
	wet = Clip.ar(wet, -0.1, 0.1);
    flt = XFade2.ar(dry, wet, eqMix);

	// Free verb //
	sig = FreeVerb.ar(flt, verbMix, room, damp, mul, add);

	// Tape Delay
	frames = ControlDur.ir * SampleRate.ir;
	buffer = LocalBuf(frames, 1).clear;
	phase = Phasor.ar(0, 1, 0, frames);

	playback = (feedback * DelayN.ar(BufRd.ar(1, buffer, phase), 15, delaytime));

	record = playback + sig;
	BufWr.ar(record, buffer, phase); // Record Signal Input plus Player input

	Out.ar(out, [record, record]);
}).add;


SynthDef(\scale, { | out=0 |
	var trig, div, freq, oct, reps, env, osc, flt;
	oct = [0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3];
	trig = Impulse.kr(2);
	env = EnvGen.kr(Env.perc(0.01, 0.4), trig, 0.5);
	freq = (Stepper.kr(trig, 0, 42, 56, 2, 42)).midicps;
	osc = Saw.ar(freq * [1, 1.01], env);
	flt = RLPF.ar(osc, freq * 2, 0.7);
	Out.ar(out, flt);
}).add;

SynthDef(\saw, { | outBus = 0, freq = 0, gate = 1, amp = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = Saw.ar(freq) * env;
	Out.ar(outBus, [sig, sig]);
}).add;

SynthDef(\myDefault, { arg out=0, freq=440, amp=0.1, pan=0, gate=1;
				var z;
				z = LPF.ar(
						Mix.new(VarSaw.ar(freq + [0, Rand(-0.4,0.0), Rand(0.0,0.4)], 0, 0.3)),
						XLine.kr(Rand(4000,5000), Rand(2500,3200), 1)
					) * Linen.kr(gate, 0.01, 0.7, 0.3, 2);
				OffsetOut.ar(out, Pan2.ar(z, pan, amp));
}).add;
)