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

		this.sendSilentMsg("/def/ready/"++name, 1); // Should this go somewhere else?

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
			var synthObj, effectObj;

			// Get the dictionaries
			synthObj = this.idGet(synthName, synthId);
			effectObj = this.idGet(effectName, effectId);

			synthObj.outBus = effectObj.inBus;
			// Set the outBus's control bus to effect inBus index
			synthObj.synth.set(\outBus, effectObj.inBus.index);

			("Connected synthrument to effect").postln;
		});

		this.addOSCResponder('/synth/disconnect/effect', { arg msg;
			var synthName = msg[1], synthId = msg[2], effectName = msg[3], effectId = msg[4];
			var synthObj, effectObj;

			synthObj = this.idGet(synthName, synthId);

			synthObj.outBus = this.getMasterIn;
			// Change synthrument's output bus back to default (0)
			synthObj.synth.set(\outBus, this.getMasterIn.index);
			("Disconnected synthrument from effect").postln;
		});

		^("OSC Responders ready");
	}
}