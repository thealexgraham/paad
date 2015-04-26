JavaHelper {

	var <>sendPort;
	var <>synthDefaults;
	var <>definitions;
	var <>ready;
	var <>readyAction;
	var <>java;
	var loaded;
	var <>pendingDefs;
	var <>defFolder;
	var masterIn;
	var didReady;

	var defs;

	getDefs { |type|
		if (defs.at(\type) == nil,
			{
				defs.put(\type, IdentityDictionary.new);
				^defs.at(\type);
			},
			{ ^defs.at(\type) }
		);
	}

	getDef { |type, name|

		^this.getDefs(\type).at(name.asSymbol);
	}

	putDef { |type, name, def|
		^this.getDefs(\type).put(name.asSymbol, def);
	}

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
		pendingDefs = Set.new;
		defs = Dictionary.new;

		ready = false;
		loaded = false;
		java = true;
		didReady = false;

		~defTasks = Dictionary.new;

		this.createListeners;
		this.createSpecialActions;

	}

	createSpecialActions {
		// These special actions are done when the patch starts up (for example)
		~loadAction = SpecialAction.new("LoadAction");
		~playAction = SpecialAction.new("PlayAction");
		~stopAction = SpecialAction.new("StopAction");
		idPut(\special, \LoadAction, ~loadAction);
		idPut(\special, \PlayAction, ~playAction);
		idPut(\special, \StopAction, ~stopAction);
	}

	createListeners {

		this.createSynthListeners;
		this.createInstListeners;
		this.createEffectListeners;
		this.createPatternPlayerListeners;
		this.createChangeFuncListeners;
		this.createChooserListeners;
		this.createPatternGenListeners;
		this.createTaskRunnerListeners;
		this.createModuleListeners;
		this.createSpecialListeners;
	}

	sendMsg { arg ... args;
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		args.postln;
		net.sendBundle(1, args);
	}

	sendSilentMsg { arg ... args;
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		net.sendBundle(1, args);
	}

	sendDefVerify { |message|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		var address = message[0] ++ "/" ++ message[1].asString ++ "/verify";
		var task;
		net.sendBundle(0, message);

		//this.addPendingDef(address);

		task = Task {
			0.25.wait;
			// Try again
			this.sendDefVerify(message);
		};
		~defTasks.put(address, task);

		OSCFunc({ |msg|
			this.removePendingDef(message[1].asSymbol);
			~defTasks.at(address).stop;
		}, address).oneShot;

		task.start;
	}

	getMasterIn {
		if (masterIn == nil,
			{
				^masterIn = Bus.audio(Server.default, 2);
			},
			{ ^masterIn; }
		);
	}

	instances {
		if (~instances == nil, {
			// Create dictionary if not created yet
			~instances = Dictionary.new;
		});

		^~instances;
	}

	// Gets whatever is at the ID (defining the type)
	idGet { |type, id|
		^this.instances.at(id);
	}

	idPut { |type, id, value|
		^this.instances.put(id, value);
	}

	idRemove { |type, id|
		^this.instances.removeAt(id);
	}

	addOSCResponder { | path, func, verify = true |
		// Can add whatever the hell function wrapping I want here
		// If has /verify as first argument, send the verification etc
		if (verify == true,
			{ OSCdef(path.asSymbol, func, path.asSymbol).verify(this.sendPort); },
			{ OSCdef(path.asSymbol, func, path.asSymbol) }
		)// Don't verify if no java
	}

	setupTypeStorage { |type|
		^type.tildaPut(Dictionary.new);
	}


	// This is all to make sure that all the synth defs are loaded before moving on

	addPendingDef { |name|
		pendingDefs.add(name);
	}

	removePendingDef { |name|
		// var net = NetAddr.new("127.0.0.1", this.sendPort);
		pendingDefs = pendingDefs.remove(name);
		this.tryReadyMessage;
	}

	tryReadyMessage {
		var size = pendingDefs.size;
		if((ready == true) && (size < 1) && (didReady == false),
			{
				javaCommand("ready");
				readyAction.value; // Run ready action
				didReady = true;
			}
		);
	}

	updateDefinition { |name, type, function, params|
		if ((type == \synth) || (type == \instrument) || (type == \effect),
			{ SynthDef.removeAt(name); });

		this.addDefinition(name, type, function, params);
	}

	/* Add a new definition, if java is ready, send them right away,
	* Otherwise, add it to the pending list
	*/
	addDefinition { |name, type, function, params|
		// Load the SynthDef if it's a SynthDef
		Routine.run {
			var c = Condition.new;

			if ((type == \synth) || (type == \instrument) || (type == \effect),
				{
					this.addPendingDef(name.asSymbol);
					SynthDef(name, function).add;
					~server.sync(c);
					// this.removePendingDef(name);
			});

			if(ready != true,
				{ definitions.put(name, (name: name, type: type, function: function, params: params)); },
				{ this.sendDefinition(name, type, function, params); }
			);

		}
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
		params = params.collect({|item|
			if (item[1].isNumber == true, //Item is missing a type
				{ 	var name = item[0];
					var min = item[1];
					var max = item[2];
					var default = item[3];
					item = [name, \float, min, max, default];
			});
			item; // put the item back in the params
		});

		// Create all the storage
		switch(type,
			\synth, {
				params = this.addDefaultParams(params);
				this.newSynth(name, function, params);
			},
			\instrument, {
				params = this.addDefaultParams(params);
				this.newInstrument(name, function, params);
			},
			\effect, {
				this.newEffect(name, function, params);
			},
			\changeFunc, {
				this.newChangeFunc(name, function, params);
			},
			\patternGen, {
				this.newPatternGen(name, function, params);
			},
			\chooser, {
				this.newChooser(name, function, params); // Sends the definition itself
			},
			\taskRunner, {
				this.storeDef(name, type, function, params);
			},
			\taskPlayer, {
				this.storeDef(name, type, function, params);
			},
			\patternPlayer, {
				this.newPatternPlayer(name, function, params);
			},
			\specialAction, {
				function = {};
				this.newSpecialAction(name, function, params);
			},
			{
				postln("No type for "++type.asString);
			}
		);

		if ((type != \chooser), {
			this.newDef(name, type, function, params); // Send the definition
		});
		// Don't need to do this if java

	}

	storeDef { |name, type, funtion, params|
		this.putDef(type, name, (function: function, params: params));
		this.sendSilentMsg("/def/ready/"++name, 1);
	}


	/*
	 * Adds the default parameters shown above
	 * only if they do not previously exist
	 */
	addDefaultParams {
		arg params, defaults = [[\gain, \float, 0.0, 1.0, 0.0], [\pan, \float, -1.0, 1.0, 0.0]];

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



/*
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
*/