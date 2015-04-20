RoutinePlayer {
	var <>template;
	var <>pattern;
	var <>playedAction;
	var <>instName;
	var <>instDict;
	var <>argsDict;
	var action;
	var instanceId;
	var listeners;

	var rout;

	*new { |id, function, arguments|
		^super.new.init(id, function, arguments);
	}

	init { |id, function, arguments|

		instanceId = id;
		pattern = nil;
		template = nil;
		listeners = IdentitySet.new;
		argsDict = Dictionary.new;
		function.postln;

		arguments.do({ |item, i|
			var name = item[0];
			var min = item[1];
			var max = item[2];
			var default = item[3];
			argsDict.put(name, ParameterBus.new(name, default, min, max));
		});

		action = function;

		this.createRout;
	}

	createRout {
		var args = [\player, this];

		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val]);
		});

		rout = action.performKeyValuePairs(\value, args); // Should return a PRout
		rout.postln;
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