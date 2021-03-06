+ JavaHelper { // patternGen Methods

	patternGenDefs {
		if (~patternGenDefs == nil,
			{
				~patternGenDefs = IdentityDictionary.new;
				^~patternGenDefs;
			},
			{ ^~patternGenDefs }
		);
	}

	getPatternGenDef { |name|
		^this.patternGenDefs.at(name.asSymbol);
	}

	putPatternGenDef { |name, def|
		^this.patternGenDefs.put(name.asSymbol, def);
	}

	/* newpatternGen
	* Tells java all about the patternGen definition
	*/
	newPatternGen { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putPatternGenDef(name, (function: function, params: params));

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

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/patterngen/add', { arg msg;
			var patternGenName = msg[1];
			var id = msg[2];
			var patternGenDef, patternGen;

			"Adding patternGen".postln;
			patternGenDef = this.getPatternGenDef(patternGenName);

			// Create the actual object
			patternGen = PatternGenerator.new(id, patternGenDef.at(\function), patternGenDef.at(\params));

			// Store the object
			patternGenName.idPut(id, patternGen);

			("patternGen added, adding patternGen at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/patterngen/remove', { arg msg;
			// Free synth defs at this id
			var patternGenName = msg[1];
			var id = msg[2];
			var patternGen = patternGenName.idGet(id);

			("patternGen disconnected, freeing patternGen at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/patterngen/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value on the pattern object
			name.idGet(id).setParam(param, val);
		});

				// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/patterngen/doaction', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2];
			name.idGet(id).doAction;
		});

		this.addOSCResponder('/patternGen/connect/param', { arg msg;
			var cfName = msg[1], cfId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var patternGen, parameter;

			patternGen = cfName.idGet(cfId);

			// Get the actual parameter object
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			patternGen.addListener(parameter); // IS THIS THE OBJECT???

			("Connected patternGens").postln;
		});

		this.addOSCResponder('/patternGen/disconnect/param', { arg msg;
			var cfName, cfId, ownerName, ownerId, paramName;
			var patternGen, parameter;

			patternGen = cfName.idGet(cfId);
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			patternGen.removeListener(parameter);

			("Connected patternGens").postln;
		});

		^("OSC Responders ready");
	}


}