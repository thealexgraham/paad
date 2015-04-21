+ JavaHelper { // RoutPlayer

	/* newchangeFunc
	* Tells java all about the changeFunc definition
	*/
	newTaskRunner { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putDef(\taskRunner, name, (function: function, params: params));

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