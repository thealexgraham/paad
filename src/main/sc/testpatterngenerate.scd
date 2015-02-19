

(

{
	|lengthMin, lengthMax, scale, root, octaveMin, octaveMax|

	var durations = [[1, 1], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];

	var finalPattern;

	var length = rrand(lengthRange[0], lengthRange[1]);
	var notes = Array.fill(length, { |i|
		var octaves = (octaveRange[0]..octaveRange[1]);
		var degree = scale.choose;
		var octave = octaves.choose;
		(root + degree) + (octave * 12);
	});

	var durationPattern = Array.newFrom(durations.choose);
	durationPattern.postln;

	finalPattern = Array.fill(length, { |i|
		var currentDuration;

		if (durationPattern.size == 0, {
			durationPattern = Array.newFrom(durations.choose);
		});

		currentDuration = durationPattern[0];
		durationPattern.removeAt(0);

		[notes[i], currentDuration];
	});

	~pattern = finalPattern.add([\rest, 8]);
}



[[\lengthMin, \int, 0, 10, 4],
	[\lengthMax, \int, 0, 16, 8],
	[\scale, \choice, ["pentatonic", [0, 3, 5, 7, 10]]], //??
	[\root, \int, 0, 127, 62],
	[\octaveMin, \int, -5, 5, 0],
	[\octaveMax, \int, -5, 5, 1],
]

Chooser.new(\scale,
	[["pentatonic", [0, 3, 5, 7, 10]],
		["minor", [0, 1, 2, 3, 4]],
		["major", [0, 5, 6, 9, 10]]
	]
);

)



(
var lengthMin = 4;
var lengthMax = 8;

var scale = [0, 3, 5, 7, 10];
var root = 62;

var octaveMin = 0;
var octaveMax = 1;

var durations = [[1, 1], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];

var finalPattern;

var length = rrand(lengthRange[0], lengthRange[1]);
var notes = Array.fill(length, { |i|
	var octaves = (octaveRange[0]..octaveRange[1]);
	var degree = scale.choose;
	var octave = octaves.choose;
	(root + degree) + (octave * 12);
});

var durationPattern = Array.newFrom(durations.choose);
durationPattern.postln;

finalPattern = Array.fill(length, { |i|
	var currentDuration;

	if (durationPattern.size == 0, {
		durationPattern = Array.newFrom(durations.choose);
	});

	currentDuration = durationPattern[0];
	durationPattern.removeAt(0);

	[notes[i], currentDuration];
});

~pattern = finalPattern.add([\rest, 8]);
)

(

~pattern = ~pattern.add([\rest, 24]);

)


(
var durations = [[1, 1], [0.5, 0.5, 1], [0.5, 0.5], [0.5, 1, 0.5], [1, 0.5]];
durations.choose
)

(
SynthDef(\sawsinInst, { | out = 0, freq = 0, gate = 1, amp = 1, saw = 1, pan = 0, gain = 1 |
		var sig, env;
		env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
		sig = SinOsc.ar(freq);
		sig = sig + (Saw.ar(freq) * saw);
		sig = sig * env * amp;
		Out.ar(out, Pan2.ar(sig, pan, gain));
	}).add;


~template = Pbind(\instrument, \default,
	\saw, 1,
	\legato, 0.9,
	\out, 0);


e = Prout({| ev |
	var pat;
	block { |break|
		loop {

			pat = Pbindf(*[
				~template,
				#[\midinote, \dur], Pseq(~pattern)
			]);
			ev = pat.embedInStream(ev);
			("Pattern played").postln;
		}
	}

}).play;




)

(
var octaveRange = [0,1];
var testArray = [9];
(octaveRange[0]..octaveRange[1]);
testArray.removeAt(0);
testArray.size
)


x = [ [1, 2, 3], 6, List["foo", 'bar']];
y = x.lace(12);