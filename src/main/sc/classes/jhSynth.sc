/*
* JavaHelper extensions
*
* SynthDefs
*
*/
+ JavaHelper { // Synth methods

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
		synthName.tildaPut(Dictionary.new);
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
				postSilent("Changing" + synthName + id + param + val);
		}).add;

		// TODO: This is the same as Inststrument's
		OSCresponder(nil, "/synth/connect/effect", { arg time, resp, msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			// Get the dictionaries
			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			synthDict.at(\outBus).set(effectDict.at(\inBus).index);

			("Connected synth to effect").postln;
		}).add;

		OSCresponder(nil, "/synth/disconnect/effect", { arg time, resp, msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Change synthrument's output bus back to default (0)
			synthDict.at(\outBus).set(0);
			("Disconnected synth from effect").postln;
		}).add;

		^("OSC Responders ready");
	}
}