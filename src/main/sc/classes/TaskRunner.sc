TaskRunner : ModuleType {
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
/*		this.createTask;*/

		playing = false;
	}

	runTask {
		var args = [\object, this];

		// Add our current arguments into the [\arg, value] array
		argsDict.pairsDo({ |key, val|
			args = args.addAll([key, val]);
		});

		Task( {
			action.performKeyValuePairs(\value, args);
			this.finished;
		}).start;

	}

	start {
		"Starting".postln;
		this.runTask;
	}

	doAction { |action|
		"Action Done".postln;
		switch ( action,
			\start, { this.start; },
			{}
		);
	}

	finished {
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