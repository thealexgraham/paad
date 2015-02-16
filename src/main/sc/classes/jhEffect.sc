+ JavaHelper { // Effect Methods

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
			net.sendMsg("/effectdef/param", effectName, param, min, max, default);
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
			msg = msg.addAll(["inBus", inBus, "outBus", 0]);
			msg.postln;

			// Create a new dictionary for this ID

			effectName.idPut(id, Dictionary.new);
			effectDict = effectName.idGet(id);

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
			var effectDict = effectName.idGet(id);

			// Free the bus
			effectDict.at(\inBus).free;
			effectDict.at(\synth).free;

			// Remove the dictionary
			effectName.idRemove(id);

			("Effect disconnected, freeing effect at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/effect/paramc", { arg time, resp, msg;
			// Set float1
			var effectName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the value directly
			effectName.idGet(id).at(\synth).set(param, val);
		}).add;

		OSCresponder(nil, "/effect/connect/effect", { arg time, resp, msg;
			var effectName = msg[1], effectId = msg[2], toEffectName = msg[3], toEffectId = msg[4];
			var toEffectDict, effectDict, toEffectInBus, effectSynth;

			// Get the destination effect's in bus
			toEffectInBus = toEffectName.idGet(toEffectId).at(\inBus);

			// Get this effect's dictionary
			effectDict = effectName.idGet(effectId);

			// Get the effect's synth and set its outBus
			effectSynth = effectDict.at(\synth);
			effectSynth.set(\outBus, toEffectInBus.index);

			("Connected effects").postln;
		}).add;

		OSCresponder(nil, "/effect/disconnect/effect", { arg time, resp, msg;
			var effectName = msg[1], effectId = msg[2];
			var effectDict, effectSynth;

			// Get dictionary and synth
			effectDict = effectName.idGet(effectId);
			effectSynth = effectDict.at(\synth);

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, 0);
			("Connected effects").postln;
		}).add;

		^("OSC Responders ready");
	}


}