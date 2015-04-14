+ JavaHelper { // RoutPlayer

	taskRunnerDefs {
		if (~taskRunnerDefs == nil,
			{
				"Adding Identity Dictionary".postln;
				~taskRunnerDefs = IdentityDictionary.new;
				^~taskRunnerDefs;
			},
			{ ^~taskRunnerDefs }
		);
	}

	getTaskRunnerDef { |name|
		^this.taskRunnerDefs.at(name.asSymbol);
	}

	putTaskRunnerDef { |name, def|
		^this.taskRunnerDefs.put(name.asSymbol, def);
	}


	/* newchangeFunc
	* Tells java all about the changeFunc definition
	*/
	newTaskRunner { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putTaskRunnerDef(name, (function: function, params: params));

		// Create the storage
		this.setupTypeStorage(name);
		net.sendMsg("/def/ready/"++name, 1);
	}



	/* createRoutListeners
	*
	* Create listeners for routine players
	*
	* Please fix this dictName nonsense
	*/
	createTaskRunnerListeners {
		var dictName = "taskrunner";
		dictName.toLower.asSymbol.envirPut(Dictionary.new);

		// Whenever an instrument is added, this will create busses for this instance of the synth
		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/taskrunner/add', { arg msg;
			var defName = msg[1];
			var id = msg[2];
			var def, taskRunner;

			"Adding changeFunc".postln;
			// The rest are the defaults
			def = this.getTaskRunnerDef(defName);

			// Create the actual function
			taskRunner = TaskRunner.new(id, def.at(\function), def.at(\params));
			defName.idPut(id, taskRunner);

			// Set the current params
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				taskRunner.setParam(paramName, value);
			});

			("TaskRunner added, adding changeFunc at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/taskrunner/remove', { arg msg;
			// Free synth defs at this id
			var name = msg[1];
			var id = msg[2];
			var taskrunner = name.idGet(id);

			("changeFunc disconnected, freeing changeFunc at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/taskrunner/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value directly
			name.idGet(id).setParam(param, val); // Change the value at the bus
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/taskrunner/play', { arg msg;
				// Set float1
			var name = msg[1];
			var id = msg[2];

			var taskRunner = name.idGet(id);
			taskRunner.play;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/taskrunner/stop', { arg msg;
				// Set float1
			var name = msg[1];
			var id = msg[2];

			var taskRunner = name.idGet(id);
			taskRunner.stop;
		});

		// New Functions
		this.addOSCResponder('/taskrunner/connect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.addListener(targetObj, action.asSymbol);

		});

		this.addOSCResponder('/taskrunner/disconnect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.removeListener(targetObj, action.asSymbol);
		});

	}

}