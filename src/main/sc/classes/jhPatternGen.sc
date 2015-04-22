+ JavaHelper { // patternGen Methods

	/* newpatternGen
	* Tells java all about the patternGen definition
	*/
	newPatternGen { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putDef(\patternGen, name, (function: function, params: params));

		// Create the storage
		this.setupTypeStorage(name);

		net.sendMsg("/def/ready/"++name, 1);
	}

	/* createpatternGenListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createPatternGenListeners {
		var defaultParams;

				// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/patterngen/doaction', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2];
			name.idGet(id).doAction;
		});

		this.addOSCResponder('/patterngen/connect/param', { arg msg;
			var cfName = msg[1], cfId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var patternGen, parameter;

			patternGen = cfName.idGet(cfId);

			// Get the actual parameter object
			parameter = ownerName.idGet(ownerId).paramAt(paramName);

			// Tell the change func to listen for this parameter
			patternGen.addListener(parameter);

			("Connected patternGens").postln;
		});

		this.addOSCResponder('/patterngen/disconnect/param', { arg msg;
			var cfName = msg[1], cfId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var patternGen, parameter;

			patternGen = cfName.idGet(cfId);
			parameter = ownerName.idGet(ownerId).paramAt(paramName);

			// Tell the change func to listen for this parameter
			patternGen.removeListener(parameter);

			("Connected patternGens").postln;
		});

		^("OSC Responders ready");
	}


}