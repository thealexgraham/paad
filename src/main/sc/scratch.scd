
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



/*		OSCresponder(nil, "/inst/playtest", { arg time, resp, msg;

			var instName = msg[1], id = msg[2];
			var templateList, busses;
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
		}).add;*/