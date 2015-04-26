TaskRepeater : ModuleType {
	var <>action;
	var action;
	var listeners;
	var task;
	var playing;

	*new { | id, name, function, arguments |
		^super.new.init(id, name, function, arguments);
	}

	init { |id, name, function, arguments|
		super.init(id, name, function, arguments);

		action = function;

		listeners = Set.new;
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

	removeSelf {
		super.removeSelf;
	}

}