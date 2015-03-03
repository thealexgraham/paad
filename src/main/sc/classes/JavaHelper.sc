JavaHelper {

	var <>sendPort;
	var <>synthDefaults;
	var <>definitions;
	var <>ready;
	var loaded;
	var pendingDefs;

	*new { |sendPort|
		^super.new.init(sendPort);
	}

	init { |sendPort|

		this.sendPort = sendPort;

		// Java will send it's listen port here when ready
		OSCresponder(nil, '/start/port', { arg time, resp, msg;
			var port = msg[1];
			var net = NetAddr("127.0.0.1", NetAddr.langPort);

			this.sendPort = port;
			("Set sendPort to "++port).postln;
			ready = true;
			this.tryReadyMessage;
			net.sendMsg("/start/ready", 1); // run.scd on run function
		}).add;

		// Java will send it's listen port here when ready
		OSCresponder(nil, '/start/port/silent', { arg time, resp, msg;
			var port = msg[1];
			var net = NetAddr("127.0.0.1", NetAddr.langPort);

			this.sendPort = port;
			("Set sendPort to "++port).postln;
			ready = true;
			//net.sendMsg("/start/ready", 1); // run.scd on run function
		}).add;

		OSCresponder(nil, '/quit', { arg time, resp, msg;
			"Quitting Server".postln;
			Server.quitAll;
		}).add;

		// Tell Java to send it's listening port
		javaCommand("setListener");

		synthDefaults = [[\gain, 0.0, 1.0, 0.0], [\pan, -1.0, 1.0, 0.0]];
		definitions = Dictionary.new;

		pendingDefs = IdentitySet.new;

		ready = false;
		loaded = false;

		this.createSynthListeners;
		this.createInstListeners;
		this.createEffectListeners;
		this.createRoutListeners;
		this.createChangeFuncListeners;
		this.createChooserListeners;
		this.createPatternGenListeners;

	}

	sendMsg { arg ... args;
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		net.sendMsg(args);
	}

	// Gets whatever is at the ID (defining the type)
	idPut { |type, id, value|
		^type.tildaGet.put(id, value);
	}

	idGet { |type, id|
		if (type.tildaGet == nil, {
			// Create dictionary if not created yet
			type.tildaPut(Dictionary.new);
		});

		^type.tildaGet.at(id);
	}

	idRemove { |type, id|
		^type.tildaGet.removeAt(id);
	}

	setupTypeStorage { |type|
		^type.tildaPut(Dictionary.new);
	}


	// This is all to make sure that all the synth defs are loaded before moving on

	addPendingDef { |name|
		pendingDefs.add(name);
	}

	removePendingDef { |name|
		pendingDefs.remove(name);
		this.tryReadyMessage;
	}

	tryReadyMessage {
		var size = pendingDefs.size;
		if((ready == true) && (size < 1),
			{
				javaCommand("ready");
			}
		);
	}

	/* Add a new definition, if java is ready, send them right away,
	* Otherwise, add it to the pending list
	*/
	addDefinition { |name, type, function, params|
		// Load the SynthDef if it's a SynthDef
		if (type == \synth ||
			type == \instrument ||
			type == \effect,
			{ SynthDef(name, function).readyLoad;});
		function.postln;
		if(ready != true,
			{ definitions.put(name, (name: name, type: type, function: function, params: params)); },
			{ sendDefinition(name, type, function, params); }
		);
	}

	/* Sends all pending instruments to java */
	sendDefinitions {
		definitions.keysValuesDo({
			|key, value|
			this.sendDefinition(value.at(\name), value.at(\type), value.at(\function), value.at(\params));
		});
	}


	/* Send a single definition to Java */
	sendDefinition { |name, type, function, params|
		function.postln;
		postln("Creating instrument " + name);

		// Create all the storage
		switch(type,
			\synth, {
				params = this.addDefaultParams(params);
				this.newSynth(name, params);
			},
			\instrument, {
				params = this.addDefaultParams(params);
				this.newInstrument(name, params);
			},
			\effect, {
				this.newEffect(name, params);
			},
			\changeFunc, {
				this.newChangeFunc(name, function, params);
			},
			\patternGen, {
				this.newPatternGen(name, function, params);
			},
			{
				postln("No type for "++type.asString);
			}
		);

		this.newDef(name, type, function, params); // Send the definition
	}



	/*
	 * Adds the default parameters shown above
	 * only if they do not previously exist
	 */
	addDefaultParams {
		arg params, defaults = [[\gain, 0.0, 1.0, 0.0], [\pan, -1.0, 1.0, 0.0]];

		var newParams = List(params.size);
		newParams.addAll(params);

		defaults.do({ |item, i|
			var contains = false;
			var param = item[0];
			params.do({ |item, i|
				if (item[0] == param,
					{ contains = true; }
				);
			});

			if (contains != true,
				{ newParams.add(item); }
			);
		});

		^newParams.asArray;
	}

	// jhEffect
	// jhSynth
	// jhInst
	// jhRoutPLayer

}