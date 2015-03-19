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

		// Add synth
		this.addOSCResponder('/synth/add', { |msg|
			var synthName = msg[1];
			var id = msg[2];
			var synthDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			msg = msg.addAll(["outBus", this.getMasterIn.index]);

			synthName.idPut(id, Dictionary.new);
			synthDict = synthName.idGet(id);

			// Store the synth and the inBus
			synthDict.put(\synth, Synth.head(~synthsGroup, synthName, msg));
			("Synth added" + id).postln;
		});

		// Remove synth
		this.addOSCResponder('/synth/remove', { |msg|
			// Free synth defs at this id
			var synthName = msg[1];
			var id = msg[2];
			var synthDict = synthName.idGet(id);

			synthDict.at(\synth).free;
			synthName.nameGet.removeAt(id);

			("Inst disconnected, freeing busses at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/synth/paramc', { arg msg;
				// Set float1
			var synthName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the bus at param
			synthName.idGet(id).at(\synth).set(param, val);

			postSilent("Changing" + synthName + id + param + val);
		});

		this.addOSCResponder('/synth/connect/effect', { arg msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			// Get the dictionaries
			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			synthDict.at(\synth).set(\outBus, effectDict.at(\inBus).index);

			("Connected synthrument to effect").postln;
		});

		this.addOSCResponder('/synth/disconnect/effect', { arg msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Change synthrument's output bus back to default (0)
			synthDict.at(\synth).set(\outBus, this.getMasterIn.index);
			("Disconnected synthrument from effect").postln;
		});

		^("OSC Responders ready");
	}
}