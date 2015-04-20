+ JavaHelper { // Effect Methods
	/* newEffect
	* Tells java all about the effect definition
	*/
	newEffect { |name, function, params|

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		this.putDef(\effect, name, (function: function, params: params));
		//effectName.tildaPut(Dictionary.new);
		this.setupTypeStorage(name);
	}

	/* createEffectListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createEffectListeners {
		var defaultParams;
		~effectsGroup = Group.tail(Server.default);

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/effect/add', { arg msg;
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
			//msg = msg.addAll(["inBus", inBus, "outBus", 0]);

			// Create a new dictionary for this ID

			effectName.idPut(id, Dictionary.new);
			effectDict = effectName.idGet(id);

			this.getMasterIn.postln;

			// Store the synth and the inBus
			effectDict.put(\synth, Synth.head(~effectsGroup, effectName,
				[\inBus, inBus, \outBus, this.getMasterIn.index]));

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 4).asInt, 4).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				var min = item[2];
				var max = item[3];
				var param = ParameterBus.new(paramName, value, min, max);
				param.ownerId = id;
				//var bus = Bus.control.set(value);

				// Save the param bus
				effectDict.put(paramName, param);
				// Map the effect to it
				effectDict.at(\synth).map(paramName, param.bus);
			});

			// Save the inBus
			effectDict.put(\inBus, inBus);

			effectDict.at(\synth).set(\outBus, this.getMasterIn);

			("Effect added, adding effect at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/effect/remove', { arg msg;
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
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/effect/paramc', { arg msg;
			// Set float1
			var effectName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the value directly
			effectName.idGet(id).at(param).setSilent(val); // Change the value at the bus
		}, false);

		this.addOSCResponder('/effect/connect/effect', { arg msg;
			var effectName = msg[1], effectId = msg[2], toEffectName = msg[3], toEffectId = msg[4];
			var toEffectDict, effectDict, toEffectInBus, effectSynth, toEffectSynth;

			toEffectSynth = toEffectName.idGet(toEffectId).synth;

			// Get the destination effect's in bus
			toEffectInBus = toEffectName.idGet(toEffectId).synth;

			// Get this effect's dictionary
			effectDict = effectName.idGet(effectId);

			// Get the effect's synth and set its outBus
			effectSynth = effectDict.synth;
			effectSynth.moveBefore(toEffectSynth);
			effectSynth.set(\outBus, toEffectInBus.index);

			("Connected effects").postln;
		});

		this.addOSCResponder('/effect/disconnect/effect', { arg msg;
			var effectName = msg[1], effectId = msg[2];
			var effectDict, effectSynth;

			// Get dictionary and synth
			effectDict = effectName.idGet(effectId);
			effectSynth = effectDict.synth;

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, this.getMasterIn);
			("Connected effects").postln;
		});

		// Disconnect this output
		this.addOSCResponder('/effect/disconnect/output', { arg msg;
			var effectName = msg[1], effectId = msg[2];
			var effectDict, effectSynth;

			// Get dictionary and synth
			effectDict = effectName.idGet(effectId);
			effectSynth = effectDict.synth;

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, this.getMasterIn);
			("Connected effects").postln;
		});
		


		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/effect/add/master', { arg msg;
			var effectName = msg[1];
			var id = msg[2];
			var effectDict;
			var inBus;
			"Adding Effect".postln;
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create a new dictionary for this ID

			effectName.idPut(id, Dictionary.new);
			effectDict = effectName.idGet(id);

			// Store the synth and the inBus
			effectDict.put(\synth, Synth.tail(~effectsGroup, effectName,
				[\inBus, this.getMasterIn, \outBus, 0]));

			~masterFader = effectDict.at(\synth);

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 4).asInt, 4).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				var min = item[2];
				var max = item[3];
				var param = ParameterBus.new(paramName, value, min, max);
				param.ownerId = id;
				//var bus = Bus.control.set(value);

				// Save the param bus
				effectDict.put(paramName, param);
				// Map the effect to it
				effectDict.at(\synth).map(paramName, param.bus);
			});
			// Save the inBus
			effectDict.put(\inBus, this.getMasterIn);

			("Effect added, adding effect at" + id).postln;
		});

		^("OSC Responders ready");
	}


}