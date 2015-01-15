JavaHelper {

	var <>sendPort;
	var <>synthDefaults;

	*new { |sendPort|
		^super.new.init(sendPort);
	}

	init { |sendPort|

		this.sendPort = sendPort;

		OSCresponder(nil, '/start/port', { arg time, resp, msg;
			var port = msg[1];
			var net = NetAddr("127.0.0.1", NetAddr.langPort);

			this.sendPort = port;
			("Set sendPort to "++port).postln;
			net.sendMsg("/start/ready", 1);
		}).add;

		OSCresponder(nil, '/quit', { arg time, resp, msg;
			"Quitting Server".postln;
			Server.quitAll;
		}).add;

		javaCommand("setListener");

		synthDefaults = [[\amp, 0.0, 1.0, 0.0], [\pan, -1.0, 0.0, 1.0]];

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

	/* oldSynth
	 * Creates the synth in java, then readys the synth to play in
	 * supercikkuder */
	oldSynth { |synthName, params|
		params = this.addDefaultParams(params);

		this.sendSynth(synthName, params);
		this.startSynth(synthName, params);
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

	/*startSynth { |synthName, params|
		var paramsSynthFormat;

		//n = NetAddr("127.0.0.1", 57120);

		// Get just the parameters and the default values
		paramsSynthFormat = Array.new(params.size * 2);
		params.do({ |item, i|
			paramsSynthFormat.add(params[i][0]);
			paramsSynthFormat.add(params[i][3]);
		});

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		synthName.toLower.asSymbol.envirPut(Dictionary.new);

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		OSCresponder(nil, "/"++synthName++"/start", { arg time, resp, msg;
			var id = msg[1];
			// Create synth defs at this location
			synthName.toLower.asSymbol.envirGet.put(id, Synth.new(synthName, paramsSynthFormat));
			("Synth connected, adding synths at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/"++synthName++"/stop", { arg time, resp, msg;
			// Free synth defs at this id
			var id = msg[1];
			synthName.toLower.asSymbol.envirGet.at(id).free;
			("Synth disconnected, freeing synths at" + id).postln;
		}).add;

		// Set up a listener for each parameter for this synth
		params.do({|item, i|
			OSCresponder(nil,"/"++synthName++"/"++item[0].asString, { arg time, resp, msg;
				// Set float1
				var id = msg[1], val = msg[2];
				synthName.toLower.asSymbol.envirGet.at(id).set(item[0], val);
				("Changing" + synthName + id + item[0] + val).postln;
			}).add;
			//("/"++synthName++"/"++item[0].asString).postln;
		});
		^("OSC Responders ready for" + synthName);
	}
*/

}