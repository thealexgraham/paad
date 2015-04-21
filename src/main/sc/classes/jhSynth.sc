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
	newSynth { |name, function, params|
		this.putDef(\synth, name, (function: function, params: params));
		// Create a dictionary to store the running synths (for multiple copies of plugin)
		name.tildaPut(Dictionary.new);
	}

	/* createSynthListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createSynthListeners {
		var defaultParams;

		~synthsGroup = Group.head(Server.default);
		
		this.addOSCResponder('/synth/connect/effect', { arg msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			// Get the dictionaries
			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			synthDict.synth.set(\outBus, effectDict.at(\inBus).index);

			("Connected synthrument to effect").postln;
		});

		this.addOSCResponder('/synth/disconnect/effect', { arg msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthDict, effectDict;

			synthDict = this.idGet(synthName, synthId);
			effectDict = this.idGet(effectName, effectId);

			// Change synthrument's output bus back to default (0)
			synthDict.synth.set(\outBus, this.getMasterIn.index);
			("Disconnected synthrument from effect").postln;
		});

		^("OSC Responders ready");
	}
}