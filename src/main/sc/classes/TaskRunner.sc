TaskRunner {
	var <>action;
	var <>argsDict;
	var instanceId;
	var action;
	var listeners;
	var task;
	var playing;

	*new { |id, function, arguments|
		^super.new.init(id, function, arguments);
	}

	init { |id, function, arguments|

		// Set the action
		// Wrap the "Reset" to the end of the function
		action = function;

		listeners = Set.new;
		argsDict = Dictionary.new;
		instanceId = id;

		arguments.do({ |item, i|
			var name = item[0];
			var min = item[1];
			var max = item[2];
			var default = item[3];
			argsDict.put(name, ParameterBus.new(name, default, min, max));
		});

		this.createTask;

		playing = false;
	}

	createTask {
		var args = [\object, this];

		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val]);
		});

		task = Task( {
			action.performKeyValuePairs(\value, args);
			this.restart;
		});

	}

	// Encapsulate this?
	paramAt { |paramName|
		^argsDict.at(paramName);
	}

	setParam { |paramName, value|
		argsDict.at(paramName).setSilent(value);
	}

	restart {
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		// task.reset;
		net.sendMsg("/"++instanceId++"/state", "reset");
		// Send stop message to client
	}

	play {
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		// Check if it is running?
		task.start;
		playing = true;
		net.sendMsg("/"++instanceId++"/state", "playing");
	}

	stop {
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		task.stop;
		task.reset;
		playing = false;
		net.sendMsg("/"++instanceId++"/state", "reset");
	}

	cycle {
		if (playing == true,
			{this.stop},
			{this.play}
		);
	}


	doAction { |action|
		switch ( action,
			\play, { this.play; },
			\stop, { this.stop; },
			\cycle, { this.cycle; },
			{}
		);
	}

	addListener { |obj, action|
		listeners.add((object: obj, action:action));
	}

	removeListener { |obj, action|
		listeners.remove((object:obj, action:action));
	}

	sendActionMessage { |obj|
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		listeners.do({ |item, i|
			item.at(\object).doAction(item.at(\action));
		});
		// JAVA ONLY
		net.sendMsg("/"++instanceId++"/action/sent", 1);
	}

}