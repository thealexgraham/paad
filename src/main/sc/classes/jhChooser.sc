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

	getChooserDef { |name|
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

		this.newChooserDef(chooserName, choices);

		// Store the definition
		this.putChooserDef(chooserName, (choices: choices));

		// Create the storage
		this.setupTypeStorage(chooserName);
		net.sendMsg("/def/ready/"++chooserName, 1);
	}

	newChooserDef { |defName, function|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		var message = ["/def/add/chooser", defName, \chooser];
		var choices = function.value; // Array of choices is what the function returns
		message = message.add(function.def.sourceCode); // Function source code for editing later

		// Go through the choices and add them to the message
		choices.do({ |item, i|
			var choiceName = item[0].asString; // First is the name of the choice
			message.add(choiceName);
			// Possibly add the actual value, but we won't do that for now
		});

		net.sendBundle(0, message);
		^("Chooser Definition Added");
	}

	/* createchooserListeners
	* responsible for setting up OSC responders for this synth and creating a unique
	* version of the synth
	*/
	createChooserListeners {
		var defaultParams;

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/chooser/add', { arg msg;
			var chooserName = msg[1];
			var id = msg[2];
			var chooserDef, chooser;

			"Adding chooser".postln;
			chooserDef = this.getChooserDef(chooserName);

			// Create the actual object
			chooser = Chooser.new(chooserDef.at(\choices).value); // choices is a funciton

			// Store the object
			chooserName.idPut(id, chooser);

			("chooser added, adding chooser at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/chooser/stop', { arg msg;
			// Free synth defs at this id
			var chooserName = msg[1];
			var id = msg[2];
			var chooser = chooserName.idGet(id);

			("chooser disconnected, freeing chooser at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/chooser/choose', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], index = msg[3];
			// Set the value on the pattern object
			name.idGet(id).postln;
			name.idGet(id).choose(index);
		});

				// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/chooser/doaction', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2];
			name.idGet(id).doAction;
		});

		this.addOSCResponder('/chooser/connect/param', { arg msg;
			var cName = msg[1], cId = msg[2], ownerName = msg[3], ownerId = msg[4], paramName = msg[5];
			var chooser, parameter;
			// Everything should match already from java
			chooser = cName.idGet(cId);

			// Get the actual parameter object
			parameter = ownerName.idGet(ownerId).paramAt(paramName);

			// Tell the change func to listen for this parameter
			chooser.addListener(parameter);

			("Connected choosers").postln;
		});

		this.addOSCResponder('/chooser/disconnect/param', { arg msg;
			var cName, cId, ownerName, ownerId, paramName;
			var chooser, parameter;

			chooser = cName.idGet(cId);
			parameter = ownerName.idGet(ownerId).at(paramName);

			// Tell the change func to listen for this parameter
			chooser.removeListener(parameter);

			("Connected choosers").postln;
		});


		^("OSC Responders ready");
	}


}