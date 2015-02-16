JavaHelper {

	var <>sendPort;
	var <>synthDefaults;
	var <>definitions;
	var <>ready;

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

		ready = false;

		this.createSynthListeners;
		this.createInstListeners;
		this.createEffectListeners;
		this.createRoutListeners;

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


	/* Add a new definition, if java is ready, send them right away,
	* Otherwise, add it to the pending list
	*/
	addDefinition { |name, type, params|
		if(ready != true,
			{ definitions.put(name, [name, type, params]); },
			{ sendDefinition(name, type, params); }
		);
	}

	/* Sends all pending instruments to java */
	sendDefinitions {
		definitions.keysValuesDo({
			|key, value|
			var name = value[0], type = value[1], params = value[2];

			this.sendDefinition(name, type, params);
		});
	}


	/* Send a single definition to Java */
	sendDefinition { |name, type, params|
		postln("Creating instrument " + name);
		switch(type,
			\synth, {
				this.newSynth(name, params);
			},
			\instrument, {
				this.newInstrument(name, params);
			},
			\effect, {
				this.newEffect(name, params);
			},
			{
				postln("No type for "++type.asString);
			}
		);
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