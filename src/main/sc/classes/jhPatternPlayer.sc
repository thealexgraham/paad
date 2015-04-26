+ JavaHelper { // RoutPlayer


	/* newRoutPlayer
	*/
	newPatternPlayer { |name, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);
		// Store the definition
		this.putDef(\patternPlayer, name, (function: function, params: params));

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
	createPatternPlayerListeners {
		var dictName = "routplayer";
		dictName.toLower.asSymbol.envirPut(Dictionary.new);

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/routplayer/play', { arg msg;
				// Set float1
			var id = msg[1];
			var player = dictName.idGet(id);

			player.play;
			("Trying to play routine player" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/routplayer/stop', { arg msg;
			var id = msg[1];
			var player = dictName.idGet(id);

			player.stop;
			("Trying to stop routine player" + id).postln;
		});

		this.addOSCResponder('/routplayer/connect/inst', { arg msg;
			// Set float1
			var id = msg[1], instName = msg[2], instId = msg[3];
			var player, instDict;

			player = dictName.idGet(id);
			instDict = instName.idGet(instId);
			player.connectInstrument(instName, instDict);

			("Connected instrument" + instName + "to player at ID"+id).postln;
		});

		this.addOSCResponder('/routplayer/remove/inst', { arg msg;
			// Set float1
			var id = msg[1];
			var player, instDict;
			player = dictName.idGet(id);
			player.removeInstrument();
			("Removed instrument").postln;
		});

		this.addOSCResponder('/routplayer/connect/pattern', { arg msg;
			// Set float1
			var id = msg[1], patternName = msg[2], patternId = msg[3];
			var player, patternObj;
			player = dictName.idGet(id);
			patternObj = patternName.idGet(patternId);
			player.connectPatternObject(patternObj);

			("Connected Pattern Object to player at ID"+id).postln;
		});

		this.addOSCResponder('/routplayer/remove/pattern', { arg msg;
			// Set float1
			var id = msg[1];
			var player, instDict;
			player = dictName.idGet(id);
			player.removeInstrument();
			("Removed instrument").postln;
		});

		// New Functions

		this.addOSCResponder('/routplayer/connect/playaction', { arg msg;
			// Set float1
			var id = msg[1], targetName = msg[2], targetId = msg[3], action = msg[4];
			var player, targetObj;

			player = dictName.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.addListener(targetObj, action.asSymbol);

			("Connected Player Object to player at ID"+id).postln;
		});

		this.addOSCResponder('/routplayer/remove/playaction', { arg msg;
			// Set float1
			var id = msg[1], targetName = msg[2], targetId = msg[3], action = msg[4];
			var player, targetObj;

			player = dictName.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.removeListener(targetObj, action.asSymbol);
			("Removed instrument").postln;
		});

	}

}