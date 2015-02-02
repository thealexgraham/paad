JavaHelper {

	var <>sendPort;
	var <>synthDefaults;

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
			net.sendMsg("/start/ready", 1); // run.scd on run function
		}).add;

		OSCresponder(nil, '/quit', { arg time, resp, msg;
			"Quitting Server".postln;
			Server.quitAll;
		}).add;

		// Tell Java to send it's listening port
		javaCommand("setListener");

		synthDefaults = [[\gain, 0.0, 1.0, 0.0], [\pan, -1.0, 1.0, 0.0]];

		this.createSynthListeners;
		this.createInstListeners;
		this.createRoutListeners;

	}

	/*
	 * Adds the default parameters shown above
	 * only if they do not previously exist
	 */
	addDefaultParams { |params|
		var newParams = List(params.size);
		newParams.addAll(params);

		synthDefaults.do({ |item, i|
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
			("Adding Param" + param + "For instrument" + instName).postln;
		});

		// Create a dictionary to store the running instruments
		instName.toLower.asSymbol.envirPut(Dictionary.new);
	}

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
			player = dictName.asString.toLower.asSymbol.envirGet.at(id);
			instDict = instName.asString.toLower.asSymbol.envirGet.at(instId);
			player.connectInstrument(instName, instDict);
			("Connected instrument" + instName + "to player at ID"+id).postln;
		}).add;
	}

	createInstListeners {
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
			instName.asString.toLower.asSymbol.envirGet.put(id, Dictionary.new);
			instDict = instName.asString.toLower.asSymbol.envirGet.at(id);

			// The rest of the parameters are pairs, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var param = item[0];
				var value = item[1];
				// Put the bus in and initialize it
				instDict.put(param, Bus.control.set(value));
			});
			("Inst added and bus created at" + id).postln;
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
			instName.asString.toLower.asSymbol.envirGet.at(id).at(param).set(val);
			("Changing" + instName + id + param + val).postln;
		}).add;


/*		OSCresponder(nil, "/inst/playtest", { arg time, resp, msg;

			var instName = msg[1], id = msg[2];
			var templateList, busses;
			var keys = List.new, args = List.new;
			var template, pattern;
			var instDict = instName.asString.toLower.asSymbol.envirGet.at(id);
			instDict.postln;

			pattern = [[5, 2], [8, 2], [10, 2], [8, 2], [\rest, 16]];

			// Create the template
			[\instrument, instName, \out, 0, \legato, 0.9].pairsDo({ |a, b|
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

			// Play our test sequence
			Pseq([
				Pbindf(template,
					//\amp, 0.2,
					#[\note, \dur], Pseq(pattern, 5),
					\octave, 4,
			)]).play;
		}).add;*/
	}


	/* sendSynth
	* Tells java all about the synth definition
	*/
	newSynth { |synthName, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		params = this.addDefaultParams(params);
		net.sendMsg("/addsynth", synthName);

		params.do({ |item, i|
			var param = item[0].asString;
			var min = item[1], max = item[2], default = item[3];
			net.sendMsg("/addparam", synthName, param, min, max, default);
		});

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		synthName.toLower.asSymbol.envirPut(Dictionary.new);
	}

	/* startSynth
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
}