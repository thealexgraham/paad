			var keys = List.new, args = List.new;
			var template, pattern;
			var instDict = instName.asString.toLower.asSymbol.envirGet.at(id);
			instDict.postln;

			pattern = [[5, 2], [8, 2], [10, 2], [8, 2], [\rest, 16]];

			// Create the template
			[\instrument, instName, \out, 0, \legato, 0.9].pairsDo({ |a, b|
				keys.add(a);
				args.add(b);
			});

			// Add the busses to the template
			instDict.keysValuesDo({ |key, value|
				keys.add(key);
				args.add(value.asMap);
			});

			// Bind the template
			template = Pbind(keys.asArray, args.asArray);

			// Play our test sequence
			Pseq([
				Pbindf(template,
					//\amp, 0.2,
					#[\note, \dur], Pseq(pattern, 5),
					\octave, 4,
			)]).play;



(
SynthDef(\sawsin, { | outBus = 0, freqAdd = 0, freq = 0, lfo = 0, saw = 0, gate = 1, amp = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = SinOsc.ar(freq + (freqAdd * 200));
	sig = sig + (Saw.ar(freq + (freqAdd * 200)) * saw);
	sig = sig * env * SinOsc.ar(lfo,0,0.5,1) * amp;
	Out.ar(outBus, [sig, sig]);
}).add;

~template = Pbind(\instrument, \sawsin,
	\saw, 1,
	\legato, 0.9,
	\out, 0);

)
~template = nil;

[~notes, ~durations].lace(~notes.size);

(
)


PatternChooser {
	var patterns;
	var weights;
	var <currentPattern;

	*new {
		super.new.init();
	}

	init {
		patterns = List.new(1);
		weights = List.new(1);
	}

	choosePattern {
		currentPattern = patterns.wchoose(weights.asArray.normalizeSum);
		^currentPattern;
	}

	addPattern { |pattern, weight|
		patterns.add(pattern);
		weights.add(weight);
	}

	removePatternAt { |index|
		patterns.removeAt(index);
		weights.removeAt(index);
	}

	changeWeightAt { |index, value|
		weights[index] = value;
	}
}

RoutinePlayer {
	var rout;
	var template;
	var pattern;
	var <>playedAction;

	*new {
		super.new.init;
	}

	init {
		this.template = nil;
		this.pattern = nil;

		rout = Prout({| ev |
			var pat;
			block { |break|
				loop {
					pat = Pbindf(*[
						this.template,
						#[\note, \dur], Pseq(pattern.value)
					]);
					this.playedAction.value(); // Run the action to do when played (probably OSC message)
					ev = pat.embedInStream(ev); // Embed the pattern and wait for it to be played
				}
			}
		});
	}

	start {
		if (pattern != nil && template != nil), {
			rout.play;
		}
	}

	stop {
		rout.stop;
	}

	connectInstrument { |instName, id|
		var instDict = instName.asString.toLower.asSymbol.envirGet.at(id);
		// Create the template
		[\instrument, instName, \out, 0].pairsDo({ |a, b|
			keys.add(a);
			args.add(b);
		});

		// Add the busses to the template
		instDict.keysValuesDo({ |key, value|
			keys.add(key);
			args.add(value.asMap);
		});

		// Bind the template
		this.template = Pbind(keys.asArray, args.asArray);
	}

	removeInstrument {
		// Stop the routine first
		rout.stop;
		this.template = nil;
	}

	setPattern { |pattern|
		this.pattern = pattern;
	}
	connectPatternObject{ |patternObject|
		this.pattern = { patternObject.getCurrentPattern; };
	}

	removePattern {
		// Stop the routine first
		rout.stop;
		this.pattern = nil;
	}

}

(
var weights, patterns;
weights = List.new(0);
patterns = List.new(0);
weights.add(1);
weights.add(2);
patterns.add([0,1,2]);
patterns.add([1,1,1]);
patterns.wchoose(weights.asArray.normalizeSum);
)


(

~currentPattern = [];

~patterns = [
	[50, [0, 1, 2, 3, 4]],
	[10, [12, 11, 10, 9]],
	[5, [1, 1, 1, 1]]
];

~patterns = [[0, 1, 2, 3, 4], [12, 11, 10, 9], [1, 1, 1, 1]];
~weights = [50, 10, 5];

~choosePattern = { |options|
	var weights, patterns;

	weights = Array.fill(options.size, { |i|
		options[i][0];
	});

	patterns = Array.fill(options.size, { |i|
		options[i][1];
	});

	patterns.wchoose(weights.normalizeSum);
};
~choosePattern.value(~patterns);

)


)

(
~mixArray = { |notes, durations|
	var newArray = Array(durations.size);
	var temp;

	notes.do({ |item, i|
		newArray.add([item, durations.wrapAt(i)]);
	});

	newArray;
};

~mixArray.value(~notes, ~durations).postln;

)

(
SynthDef(\sawsin, { | outBus = 0, freqAdd = 0, freq = 0, lfo = 0, saw = 0, gate = 1, amp = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = SinOsc.ar(freq + (freqAdd * 200));
	sig = sig + (Saw.ar(freq + (freqAdd * 200)) * saw);
	sig = sig * env * SinOsc.ar(lfo,0,0.5,1) * amp;
	Out.ar(outBus, [sig, sig]);
}).add;

~template = Pbind(\instrument, \default,
	\saw, 1,
	\legato, 0.9,
	\out, 0);

)

(
~durations = [0.25,0.25];
~notes = [1, 0, 7, 1, 0];

~mixArray.value(~notes, ~durations);

e = Prout({| ev |
	var pat;
	block { |break|
		loop {

			pat = Pbindf(*[
				~template,
				dur: Pseq(~durations, inf),
				note: Pseq(~notes, 1)
			]);
			~notes = Array.fill(20, { 10.rand });
			ev = pat.embedInStream(ev);
			("Pattern played").postln;
		}
	}

});

)

e.stop

e = e.play


// With break




e = Prout({| ev |
	var pat;
	block { |break|
		loop {
			if (~template != nil,
				{
					pat = Pbindf(*[
						~template,
						dur: Pseq(~durations, inf),
						note: Pseq(~notes, 1)
					]);
					~notes = Array.fill(20, { 10.rand });
					ev = pat.embedInStream(ev);
					("Pattern played").postln;
				}, {
					("No template").postln;
					break.value;
					ev = nil;
			});

		}
	}

}).play;