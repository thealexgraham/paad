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
	var playing;

	var patterns;

	var rout;

	*new { |id, function, arguments|
		^super.new.init(id, function, arguments);
	}

	init { |id, function, arguments|


		pattern = nil;
		template = nil;

		listeners = IdentitySet.new;

		patterns = IdentitySet.new;

		instanceId = id;
		argsDict = ModuleType.setupParams(instanceId, arguments);


		action = function;
		playing = false;
		this.createRout;


	}

	createRout {
		var args = [\player, this];
		var choice;

		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val]);
		});

		rout = action.performKeyValuePairs(\value, args); // Should return a PRout
	}

	play {
		if ((template != nil), {
			"Playing routine";
			rout = rout.play;
			playing = true;
		});
	}

	stop {
		"Stopping routine".postln;
		rout.stop;
		playing = false;
	}

	playbutton {
		if (playing == true,
			{this.stop},
			{this.play}
		);
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



	connectInstrument { |instName, inst|
		var templateList, busses;
		var keys = List.new;
		var args = List.new;

		rout.stop;

		// Create the template (non busses)
		[\instrument, instName].pairsDo({ |a, b|
			keys.add(a);
			args.add(b);
		});

		// Add the busses to the template
		inst.getParams.keysValuesDo({ |name, param|
			keys.add(name);
			args.add(param.bus.asMap);
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

	connectPattern { |paramName, obj|
		patterns.put(paramName, obj);
	}

	connectPatternObject { |patternObject|
		postln("Trying to set pattern pboject");

		// Pattern is a function returning the current pattern
		pattern = {
			patternObject.getCurrentPattern;
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
			\playbutton, { this.playbutton; },
			{}
		);
	}


	// Will be inherited
	paramAt {|paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		argsDict.at(paramName).setSilent(value);
	}

	removeSelf {
		argsDict.keysValuesArrayDo( {|key, value|
			value.bus.free
		});
	}

}