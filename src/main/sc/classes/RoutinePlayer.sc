RoutinePlayer {
	var <>template;
	var <>pattern;
	var <>playedAction;
	var rout;

	*new {
		^super.new.init;
	}

	init {

		pattern = nil;
		template = nil;

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

	play {
		if ((pattern != nil) && (template != nil), {
			"Playing routine";
			rout = rout.play;
		});
	}

	stop {
		"Stopping routine".postln;
		rout.stop;
	}

	connectInstrument { |instName, instDict|
		var templateList, busses;
		var keys = List.new, args = List.new;

		"Creating template".postln;
		rout.stop;

		// Create the template
		[\instrument, instName].pairsDo({ |a, b|
			keys.add(a);
			args.add(b);
		});

		keys.add(\out);
		args.add(instDict.at(\out));
		"ISNTASD".postln;
		instDict.at(\out).postln;


		// Add the busses to the template
		instDict.keysValuesDo({ |key, value|
			if(key != \out, {
				keys.add(key);
				args.add(value.asMap);
			});
		});

				keys.postln;
		args.postln;

		// Bind the template
		template = Pbind(keys.asArray, args.asArray);
	}

	removeInstrument {
		// Stop the routine first
		rout.stop;
		template = nil;
	}


	setPattern { |newPattern|
		pattern = newPattern;
	}

	connectPatternObject { |patternObject|
		pattern = {
			//patternObject.getCurrentPattern;
			patternObject.choosePattern;
		};
	}

	removePattern {
		// Stop the routine first
		rout.stop;
		pattern = nil;
	}

}