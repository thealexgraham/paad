+ JavaHelper {

	newInstrument { |instName, params|
		// Create a dictionary to store the running instruments
		instName.tildaPut(Dictionary.new);
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
			instName.idPut(id, Dictionary.new);
			instDict = instName.idGet(id);

			instDict.put(\outBus, Bus.control.set(0)); // Default out bus is 0

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
			var instDict = instName.idGet(id);

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
			instName.idGet(id).at(param).set(val);

			postSilent("Changing" + instName + id + param + val);
		}).add;

		OSCresponder(nil, "/inst/connect/effect", { arg time, resp, msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var instDict, effectDict;

			// Get the dictionaries
			instDict = this.idGet(instName, instId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			instDict.at(\outBus).set(effectDict.at(\inBus).index);

			("Connected instrument to effect").postln;
		}).add;

		OSCresponder(nil, "/inst/disconnect/effect", { arg time, resp, msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var instDict, effectDict;

			instDict = this.idGet(instName, instId);
			effectDict = this.idGet(effectName, effectId);

			// Change instrument's output bus back to default (0)
			instDict.at(\outBus).set(0);
			("Disconnected instrument from effect").postln;
		}).add;
	}
}