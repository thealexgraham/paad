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


	newInstrument { |instName, params|
		// Tell Java about the instrument
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		params = this.addDefaultParams(params);
		net.sendMsg("/instdef/add", instName);

		params.do({ |item, i|
			var param = item[0].asString;
			var min = item[1], max = item[2], default = item[3];
			net.sendMsg("/instdef/param", instName, param, min, max, default);
			// ("Adding Param" + param + "For instrument" + instName).postln;
		});

		// Create a dictionary to store the running instruments
		instName.toLower.asSymbol.envirPut(Dictionary.new);
	}

	createInstListeners {
		~instrumentGroup = Group.head(Server.default);

		// Whenever an instrument is added, this will create busses for this instance of the synth
		OSCresponder(nil, "/inst/add", { arg time, resp, msg;
			var instName = msg[1];
			var id = msg[2];
			var instDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create a dictionary of busses for this instrument at its ID
			instName.tildaGet.put(id, Dictionary.new);
			instDict = instName.tildaGet.at(id);

			instDict.put(\out, 0); // Default out bus is 0

			// The rest of the parameters are pairs, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var param = item[0];
				var value = item[1];
				// Put the bus in and initialize it
				instDict.put(param, Bus.control.set(value));
			});

			("Inst added and busses created at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/inst/remove", { arg time, resp, msg;
			// Free synth defs at this id
			var instName = msg[1];
			var id = msg[2];
			var instDict = instName.asString.toLower.asSymbol.envirGet.at(id);

			// Free the busses and remove them from this dictionary (is this necessary?)
			instDict.keysValuesArrayDo({ |key, value|
				value.free;
				instDict.removeAt(key);
			});


			("Inst disconnected, freeing busses at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/inst/paramc", { arg time, resp, msg;
				// Set float1
			var instName = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the bus at param
			instName.tildaGet.at(id).at(param).set(val);
			//("Changing" + instName + id + param + val).postln;
		}).add;

		OSCresponder(nil, "/inst/connect/effect", { arg time, resp, msg;
/*			var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
			var instDict, effectDict;*/
			("received " + msg).postln;

/*			instDict = instName.tildaGet.at(instId);
			effectDict = this.idGet(effectName, effectId);

			instDict.put(\outBus, effectDict.at(\inBus));*/
			("Connected instrument to effect").postln;
		}).add;

		OSCresponder(nil, "/inst/disconnect/effect", { arg time, resp, msg;
			var instName = msg[1], instId = msg[2], effectName = msg[4], effectId = msg[5];
			var instDict, effectDict;

			instDict = instName.tildaGet.at(instId);
			effectDict = effectName.tildaGet.at(effectId); // Don't really need this

			// Change instrument's output back to default (0)
			instDict.put(\outBus, 0);
			("Disconnected instrument from effect").postln;
		}).add;
	}

	/* newEffect
	* Tells java all about the effect definition
	*/
	newEffect { |effectName, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		// Need default params for effect?
		params = this.addDefaultParams(params, [[\gain, 0.0, 1.0, 0.0]]);

		// Get effect ready in Java
		net.sendMsg("/effectdef/add", effectName);

		// Should we wait for callback?
		params.do({ |item, i|
			var param = item[0].asString;
			var min = item[1], max = item[2], default = item[3];
			net.sendMsg("/addparam", effectName, param, min, max, default);
		});

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		//effectName.tildaPut(Dictionary.new);
		this.setupTypeStorage(effectName);
	}

	/* createEffectListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createEffectListeners {
		var defaultParams;
		~effectsGroup = Group.tail(Server.default);

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		OSCresponder(nil, "/effect/add", { arg time, resp, msg;
			var effectName = msg[1];
			var id = msg[2];
			var effectDict;
			var inBus;
			"Adding Effect".postln;
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			inBus = Bus.audio(Server.default, 2);

			// Add inBus and outBus to the default arguments
			msg.add("inBus", inBus);
			msg.add("outBus", 0); // Default go straight out

			// Create a new dictionary for this ID
			//effectName.tildaGet.put(id, Dictionary.new);
			this.idPut(effectName, id, Dictionary.new);
			effectDict = this.idGet(effectName, id);

			// effectDict = effectName.tildaGet.at(id);

			// Store the synth and the inBus
			effectDict.put(\synth, Synth.tail(~effectsGroup, effectName, msg));
			effectDict.put(\inBus, inBus);
			("Effect added, adding effect at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/effect/stop", { arg time, resp, msg;
			// Free synth defs at this id
			var effectName = msg[1];
			var id = msg[2];
			var effectDict = this.idGet(effectName, id);

			// Free the bus
			effectDict.at(\inBus).free;
			effectDict.at(\synth).free;

			// Remove the dictionary
			this.idRemove(effectName, id);

			("Effect disconnected, freeing effect at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/effect/paramc", { arg time, resp, msg;
			// Set float1
			var effectName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the value directly
			this.idGet(effectName, id).at(\synth).set(param, val);
			("Changing" + effectName + id + param + val).postln;
		}).add;

		OSCresponder(nil, "/effect/connect/effect", { arg time, resp, msg;
			var effectName = msg[1], effectId = msg[2], toEffectName = msg[4], toEffectId = msg[5];
			var toEffectDict, effectDict, toEffectInBus, effectSynth;

			// Get the destination effect's in bus
			toEffectInBus = this.idGet(toEffectName, toEffectId).at(\inBus);
			// Get this effect's dictionary
			effectDict = this.idGet(effectName, effectId);

			// Get the effect's synth and set its outBus
			effectSynth = effectDict.at(\synth);
			effectSynth.set(\outBus, toEffectInBus);
			("Connected effects").postln;
		});

		OSCresponder(nil, "/effect/disconnect/effect", { arg time, resp, msg;
			var effectName = msg[1], effectId = msg[2];
			var effectDict, effectSynth;

			// Get dictionary and synth
			effectDict = this.idGet(effectName, effectId);
			effectSynth = effectDict.at(\synth);

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, 0);
			("Connected effects").postln;
		});

		^("OSC Responders ready");
	}


	/* newSynth
	* Tells java all about the synth definition
	*/
	newSynth { |synthName, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		params = this.addDefaultParams(params);
		net.sendMsg("/synthdef/add", synthName);

		params.do({ |item, i|
			var param = item[0].asString;
			var min = item[1], max = item[2], default = item[3];
			net.sendMsg("/addparam", synthName, param, min, max, default);
		});

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		synthName.toLower.asSymbol.envirPut(Dictionary.new);
	}

	/* createSynthListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createSynthListeners {
		var defaultParams;

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		OSCresponder(nil, "/synth/start", { arg time, resp, msg;
			var synthName = msg[1];
			var id = msg[2];

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create synth defs at this location
			synthName.asString.toLower.asSymbol.envirGet.put(id, Synth.new(synthName, msg));
			("Synth connected, adding synths at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/synth/stop", { arg time, resp, msg;
			// Free synth defs at this id
			var synthName = msg[1];
			var id = msg[2];
			synthName.asString.toLower.asSymbol.envirGet.at(id).free;
			("Synth disconnected, freeing synths at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/synth/paramc", { arg time, resp, msg;
				// Set float1
			var synthName = msg[1], param = msg[2], id = msg[3], val = msg[4];
				synthName.asString.toLower.asSymbol.envirGet.at(id).set(param, val);
				("Changing" + synthName + id + param + val).postln;
		}).add;

		^("OSC Responders ready");
	}

	/* createRoutListeners
	*
	* Create listeners for routine players
	*
	* Please fix this dictName nonsense
	*/
	createRoutListeners {
		var dictName = "routplayer";
		dictName.toLower.asSymbol.envirPut(Dictionary.new);

		// Whenever an instrument is added, this will create busses for this instance of the synth
		OSCresponder(nil, "/routplayer/add", { arg time, resp, msg;
			var id = msg[1];
			var player = RoutinePlayer.new;
			var chooser = PatternChooser.new;
			// test pattern for now
			player.pattern = [[1,1], [2, 0.5], [1, 0.5], [10, 1]];

			chooser.addPattern([[1,1], [2, 0.5], [1, 0.5]], 50);
			chooser.addPattern([[5,1], [5, 0.5], [5, 0.5]], 50);
			chooser.addPattern([[10,1], [5, 0.5], [10, 0.5],[10, 0.2],[10, 0.7]], 25);
			player.connectPatternObject(chooser);

			dictName.toLower.asSymbol.envirGet.put(id, player);
			("Routine player created at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/routplayer/remove", { arg time, resp, msg;
			// Free synth defs at this id
			var id = msg[1];
			dictName.toLower.asSymbol.envirGet.removeAt(id);
			("Routine player removed at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/routplayer/play", { arg time, resp, msg;
				// Set float1
			var id = msg[1];
			var player = dictName.asString.toLower.asSymbol.envirGet.at(id);
			player.play;
			("Trying to play routine player" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/routplayer/stop", { arg time, resp, msg;
				// Set float1
			var id = msg[1];
			var player = dictName.asString.toLower.asSymbol.envirGet.at(id);
			player.stop;
			("Trying to stop routine player" + id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/connect/inst", { arg time, resp, msg;
			// Set float1
			var id = msg[1], instName = msg[2], instId = msg[3];
			var player, instDict;
			"trying to connect instrument".postln;

			player = dictName.asString.toLower.asSymbol.envirGet.at(id);
			instDict = instName.asString.toLower.asSymbol.envirGet.at(instId);
			player.connectInstrument(instName, instDict);

			("Connected instrument" + instName + "to player at ID"+id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/remove/inst", { arg time, resp, msg;
			// Set float1
			var id = msg[1];
			var player, instDict;
			player = dictName.asString.toLower.asSymbol.envirGet.at(id);
			player.removeInstrument();
			("Removed instrument").postln;
		}).add;
	}

}