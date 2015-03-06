+ JavaHelper { // chooser Methods

	chooserDefs {
		if (~chooserDefs == nil,
			{
				~chooserDefs = IdentityDictionary.new;
				^~chooserDefs;
			},
			{ ^~chooserDefs }
		);
	}

	getChooserDefs { |name|
		^this.chooserDefs.at(name.asSymbol);
	}

	putChooserDef { |name, def|
		^this.chooserDefs.put(name.asSymbol, def);
	}

	/* newchooser
	* Tells java all about the chooser definition
	*/
	newChooser { |chooserName, choices|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr

		// Get chooser ready in Java
		net.sendMsg("/chooserdef/add", chooserName);

		// Should we wait for callback?
		choices.do({ |item, i|
			var choiceName = item[0];
			var choiceArray = item[1]; //lets assume it is a single dimension array for now
			// Create the message as a list
			var message = choiceArray.insert(0, choiceName).insert(0, chooserName).insert(0, "/chooserdef/param");
			net.sendBundle(0, message);
		});

		// Store the definition
		this.putChooserDef(chooserName, (choices: choices));

		// Create the storage
		this.setupTypeStorage(chooserName);
		net.sendMsg("/defs/ready/"++chooserName, 1);
	}

	/* createchooserListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createChooserListeners {
		var defaultParams;

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		OSCresponder(nil, "/chooser/add", { arg time, resp, msg;
			var chooserName = msg[1];
			var id = msg[2];
			var chooserDef, chooser;

			"Adding chooser".postln;
			chooserDef = this.getchooserDef(chooserName);

			// Create the actual object
			chooser = chooser.new(chooserDef.at(\choices));

			// Store the object
			chooserName.idPut(id, chooser);

			("chooser added, adding chooser at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/chooser/stop", { arg time, resp, msg;
			// Free synth defs at this id
			var chooserName = msg[1];
			var id = msg[2];
			var chooser = chooserName.idGet(id);

			("chooser disconnected, freeing chooser at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/chooser/choose", { arg time, resp, msg;
			// Set float1
			var name = msg[1], id = msg[2], index = msg[3];
			// Set the value on the pattern object
			name.idGet(id).choose(index);
		}).add;

				// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/chooser/doaction", { arg time, resp, msg;
			// Set float1
			var name = msg[1], id = msg[2];
			name.idGet(id).doAction;
		}).add;

		OSCresponder(nil, "/chooser/connect/param", { arg time, resp, msg;
			var cName = msg[1], cId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var chooser, parameter;
			// Everything should match already from java
			chooser = cName.idGet(cId);

			// Get the actual parameter object
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			chooser.addListener(parameter);

			("Connected choosers").postln;
		}).add;

		OSCresponder(nil, "/chooser/disconnect/param", { arg time, resp, msg;
			var cName, cId, ownerName, ownerId, paramName;
			var chooser, parameter;

			chooser = cName.idGet(cId);
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			chooser.removeListener(parameter);

			("Connected choosers").postln;
		}).add;


		^("OSC Responders ready");
	}


}