+ JavaHelper {

	newInstrument { |name, function, params|
		this.putDef(\instrument, name, (function: function, params: params));
	}

	createInstListeners {
		~instrumentGroup = Group.head(Server.default);

		// Whenever an instrument is added, this will create busses for this instance of the synth
		this.addOSCResponder('/inst/add', { arg msg;
			var instName = msg[1];
			var id = msg[2];
			var instDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create a dictionary of busses for this instrument at its ID
			instName.idPut(id, Dictionary.new);
			instDict = instName.idGet(id);

			instDict.put(\outBus, Bus.control.set(this.getMasterIn.index)); // Default out bus is 0

			// The rest of the parameters are pairs, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var param = item[0];
				var value = item[1];
				// Put the bus in and initialize it
				instDict.put(param, Bus.control.set(value));
			});

			("Inst added and busses created at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/inst/remove', { arg msg;
			// Free synth defs at this id
			var instName = msg[1];
			var id = msg[2];
			var instDict = instName.idGet(id);

			// Free the busses and remove them from this dictionary (is this necessary?)
			instDict.keysValuesArrayDo({ |key, value|
				value.free;
				instDict.removeAt(key);
			});

			("Inst disconnected, freeing busses at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/inst/paramc', { arg msg;
				// Set float1
			var instName = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the bus at param
			instName.idGet(id).at(param).set(val);

			postSilent("Changing" + instName + id + param + val);
		});

		this.addOSCResponder('/inst/connect/effect', { arg msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var inst, effectDict;

			// Get the dictionaries
			inst = this.idGet(instName, instId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			inst.paramAt(\outBus).bus.set(effectDict.at(\inBus).index);

			("Connected instrument to effect").postln;
		});

		this.addOSCResponder('/inst/disconnect/effect', { arg msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var inst, effectDict;

			inst = this.idGet(instName, instId);
			effectDict = this.idGet(effectName, effectId);

			// Change instrument's output bus back to default (0)
			inst.paramAt(\outBus).bus.set(this.getMasterIn.index);
			("Disconnected instrument from effect").postln;
		});
	}
}