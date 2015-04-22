+ JavaHelper { // Effect Methods
	/* newEffect
	* Tells java all about the effect definition
	*/
	newEffect { |name, function, params|

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		this.putDef(\effect, name, (function: function, params: params));
		//effectName.tildaPut(Dictionary.new);
		this.setupTypeStorage(name);

		this.sendSilentMsg("/def/ready/"++name, 1); // Should this go somewhere else?

	}

	/* createEffectListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createEffectListeners {
		var defaultParams;
		~effectsGroup = Group.tail(Server.default);

		this.addOSCResponder('/effect/connect/effect', { arg msg;
			var effectName = msg[1], effectId = msg[2], toEffectName = msg[3], toEffectId = msg[4];
			var toEffect, effect, toEffectInBus, effectSynth, toEffectSynth;

			toEffect = toEffectName.idGet(toEffectId);
			toEffectSynth = toEffect.synth;

			// Get the destination effect's in bus
			toEffectInBus = toEffectName.idGet(toEffectId).inBus;

			// Get this effect's dictionary
			effect = effectName.idGet(effectId);

			// Get the effect's synth and set its outBus
			effectSynth = effect.synth;
			effectSynth.moveBefore(toEffectSynth);
			effectSynth.set(\outBus, toEffectInBus.index);

			("Connected effects").postln;
		});

		this.addOSCResponder('/effect/disconnect/effect', { arg msg;
			var effectName = msg[1], effectId = msg[2];
			var effect, effectSynth;

			// Get dictionary and synth
			effect = effectName.idGet(effectId);
			effectSynth = effect.synth;

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, this.getMasterIn);
			("Connected effects").postln;
		});

		// Disconnect this output
		this.addOSCResponder('/effect/disconnect/output', { arg msg;
			var effectName = msg[1], effectId = msg[2];
			var effect, effectSynth;

			// Get dictionary and synth
			effect = effectName.idGet(effectId);
			effectSynth = effect.synth;

			// Set outBus back to 0 since it isn't connected to anything
			effectSynth.set(\outBus, this.getMasterIn);
			("Connected effects").postln;
		});

		this.addOSCResponder('/effect/add/master', { arg msg;
			var type = msg[1].asSymbol;
			var name = msg[2];
			var id = msg[3];
			var def, module;

			msg.removeAt(0); // Address
			msg.removeAt(0); // Type
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults
			def = this.getDef(type, name);

			// Create the actual function
			module = EffectModule.newMaster(id, name, def.at(\function), def.at(\params));
			name.idPut(id, module);

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				module.setParam(paramName, value);
			});

			("Module added at" + id).postln;
		});
	}


}