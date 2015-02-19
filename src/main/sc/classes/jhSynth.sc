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
			net.sendMsg("/synthdef/param", synthName, param, min, max, default);
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

		~synthsGroup = Group.head(Server.default);

		// Whenever an synthrument is added, this will create busses for this synthance of the synth
		OSCresponder(nil, "/synth/add", { arg time, resp, msg;
			var synthName = msg[1];
			var id = msg[2];
			var synthDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			msg = msg.addAll(["outBus", 0]);

			synthName.idPut(id, Dictionary.new);
			synthDict = synthName.idGet(id);

			// Store the synth and the inBus
			synthDict.put(\synth, Synth.head(~synthsGroup, synthName, msg));

			("Synth added" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/synth/remove", { arg time, resp, msg;
			// Free synth defs at this id
			var synthName = msg[1];
			var id = msg[2];
			var synthDict = synthName.idGet(id);

			synthDict.at(\synth).free;
			synthName.nameGet.removeAt(id);

			("Inst disconnected, freeing busses at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/synth/paramc", { arg time, resp, msg;
				// Set float1
			var synthName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the bus at param
			synthName.idGet(id).at(\synth).set(param, val);

			postSilent("Changing" + synthName + id + param + val);
		}).add;

		OSCresponder(nil, "/synth/connect/effect", { arg time, resp, msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			// Get the dictionaries
			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			synthDict.at(\synth).set(\outBus, effectDict.at(\inBus).index);

			("Connected synthrument to effect").postln;
		}).add;

		OSCresponder(nil, "/synth/disconnect/effect", { arg time, resp, msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Change synthrument's output bus back to default (0)
			synthDict.at(\synth).set(\outBus, 0);
			("Disconnected synthrument from effect").postln;
		}).add;

		^("OSC Responders ready");
	}
}