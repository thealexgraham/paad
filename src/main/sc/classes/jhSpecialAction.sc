+ JavaHelper {
	
	/* newchangeFunc
	* Tells java all about the changeFunc definition
	*/
	newSpecialAction { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		net.sendMsg("/def/ready/"++name, 1);
	}

	createSpecialListeners {

		// New Functions
		this.addOSCResponder('/special/action', { arg msg;
			// Set float1
			var name = msg[1];
			var id = msg[2];
			var player;
			player = name.idGet(id);
			player.sendActionMessage;
		});

		// New Functions
		this.addOSCResponder('/special/connect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.addListener(targetObj, action.asSymbol);

		});

		this.addOSCResponder('/special/disconnect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2].asString, targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.removeListener(targetObj, action.asSymbol);
		});
	}

}