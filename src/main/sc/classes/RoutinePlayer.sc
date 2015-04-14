RoutinePlayer {
	var <>template;
	var <>pattern;
	var <>playedAction;
	var <>instName;
	var <>instDict;
	var instanceId;
	var listeners;

	var rout;

	*new { |id|
		^super.new.init(id);
	}

	init { |id|

		instanceId = id;
		pattern = nil;
		template = nil;
		listeners = IdentitySet.new;

		rout = Prout({| ev |
			var pat;
			block { |break|
				loop {
					pat = Pbindf(*[
						this.template,
						#[\midinote, \dur], Pseq(pattern.value)
					]);
					this.doPlayedAction; // Run the action to do when played (probably OSC message)
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

	addListener { |obj|
		listeners.add(obj);
	}

	removeListener { |obj|
		listeners.remove(obj);
	}

	doPlayedAction { |obj|
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		// JAVA ONLY
		net.sendMsg("/"++instanceId++"/action/sent", 1);

		listeners.do({ |item, i|
			item.doAction;
		});
	}


	isInstConnected {
		if ((instDict == nil || instName == nil),
			{ ^true },
			{ ^false});
	}



	connectInstrument { |instName, instDict|
		var templateList, busses;
		var keys = List.new;
		var args = List.new;

		"Creating template".postln;
		rout.stop;

		// Create the template (non busses)
		[\instrument, instName].pairsDo({ |a, b|
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
		postln("Trying to set pattern pboject");
		patternObject.postln;
		pattern = {
			patternObject.getCurrentPattern;
			//patternObject.choosePattern;
		};
	}

	removePattern {
		// Stop the routine first
		rout.stop;
		pattern = nil;
	}

	doAction { |action|
		switch ( action,
			\play, { this.play; },
			\stop, { this.stop; },
			{}
		);
	}

}