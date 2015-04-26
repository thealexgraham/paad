+ JavaHelper { // changeFunc Methods

	/* newchangeFunc
	* Tells java all about the changeFunc definition
	*/
	newChangeFunc { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putDef(\changeFunc, name, (function: function, params: params));

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
			owner = ownerName.idGet(ownerId);

			if (owner.class == Dictionary,
				{
					parameter = ownerName.idGet(ownerId).at(paramName);
				}, {
					parameter = owner.paramAt(paramName);
				}
			);

			// Tell the change func to listen for this parameter
			changeFunc.removeListener(parameter);

			("Disconnected changeFuncs").postln;
		});


		^("OSC Responders ready");
	}


}