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
		this.addOSCResponder('/player/play', { arg msg;
				// Set float1
			var name = msg[1];
			var id = msg[2];

			var player = name.idGet(id);
			player.play;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/player/stop', { arg msg;
				// Set float1
			var name = msg[1];
			var id = msg[2];

			var player = name.idGet(id);
			player.stop;
		});

	}

}