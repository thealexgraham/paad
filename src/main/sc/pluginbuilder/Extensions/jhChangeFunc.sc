+ JavaHelper { // changeFunc Methods

	changeFuncDefs {
		if (~changeFuncDefs == nil,
			{
				"Adding Identity Dictionary".postln;
				~changeFuncDefs = IdentityDictionary.new;
				^~changeFuncDefs;
			},
			{ ^~changeFuncDefs }
		);
	}

	getChangeFuncDef { |name|
		^this.changeFuncDefs.at(name.asSymbol);
	}

	putChangeFuncDef { |name, def|
		^this.changeFuncDefs.put(name.asSymbol, def);
	}

	/* newchangeFunc
	* Tells java all about the changeFunc definition
	*/
	newChangeFunc { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putChangeFuncDef(name, (function: function, params: params));

		// Create the storage
		this.setupTypeStorage(name);
		net.sendMsg("/def/ready/"++name, 1);
	}

	/* createchangeFuncListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createChangeFuncListeners {
		var defaultParams;

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/changefunc/add', { arg msg;
			var changeFuncName = msg[1];
			var id = msg[2];
			var changeFuncDef, changeFunc;

			"Adding changeFunc".postln;
			// The rest are the defaults
			changeFuncDef = this.getChangeFuncDef(changeFuncName);

			// Create the actual function
			changeFunc = ChangeFunc.new(changeFuncDef.at(\function), changeFuncDef.at(\params));
			changeFuncName.idPut(id, changeFunc);

			("changeFunc added, adding changeFunc at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/changefunc/remove', { arg msg;
			// Free synth defs at this id
			var changeFuncName = msg[1];
			var id = msg[2];
			var changeFunc = changeFuncName.idGet(id);

			("changeFunc disconnected, freeing changeFunc at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/changefunc/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value directly
			name.idGet(id).setParam(param, val); // Change the value at the bus
		});

				// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/changefunc/doaction', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2];
			name.idGet(id).doAction;
		});

		this.addOSCResponder('/changefunc/connect/param', { arg msg;
			var cfName = msg[1], cfId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var changeFunc, parameter, owner;

			changeFunc = cfName.idGet(cfId);

			owner = ownerName.idGet(ownerId);

			// Get the actual parameter object

			if (owner.class == Dictionary,
				{
					parameter = ownerName.idGet(ownerId).at(paramName);
				}, {
					parameter = owner.paramAt(paramName);
				}
			);
			// Tell the change func to listen for this parameter
			changeFunc.addListener(parameter); // IS THIS THE OBJECT???

			("Connected changeFuncs").postln;
		});

		this.addOSCResponder('/changefunc/disconnect/param', { arg msg;
			var cfName = msg[1], cfId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var changeFunc, owner, parameter;

			changeFunc = cfName.idGet(cfId);
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			changeFunc.removeListener(parameter);

			("Disconnected changeFuncs").postln;
		});


		^("OSC Responders ready");
	}


}