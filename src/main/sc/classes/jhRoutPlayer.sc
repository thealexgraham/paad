+ JavaHelper { // RoutPlayer

	/* createRoutListeners
	*
	* Create listeners for routine players
	*
	* Please fix this dictName nonsense
	*/
	createRoutListeners {
		var dictName = "routplayer";
		dictName.toLower.asSymbol.envirPut(Dictionary.new);

		// Whenever an instrument is added, this will create busses for this instance of the synth
		this.addOSCResponder('/routplayer/add', { arg msg;
			var id = msg[1];
			var player = RoutinePlayer.new(id);
			var chooser = PatternChooser.new;
			// test pattern for now
			player.pattern = [[1,1], [2, 0.5], [1, 0.5], [10, 1]];

			chooser.addPattern([[1,1], [2, 0.5], [1, 0.5]], 50);
			chooser.addPattern([[5,1], [5, 0.5], [5, 0.5]], 50);
			chooser.addPattern([[10,1], [5, 0.5], [10, 0.5],[10, 0.2],[10, 0.7]], 25);
			player.connectPatternObject(chooser);

			this.idPut(dictName, id, player).postln;

			this.idGet(dictName, id).postln;
			("Routine player created at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/routplayer/remove', { arg msg;
			// Free synth defs at this id
			var id = msg[1];
			dictName.idRemove(id);
			("Routine player removed at" + id).postln;
		});

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
				// Set float1
			var id = msg[1];
			var player = dictName.idGet(id);
			player.stop;
			("Trying to stop routine player" + id).postln;
		});

		this.addOSCResponder('/routplayer/connect/inst', { arg msg;
			// Set float1
			var id = msg[1], instName = msg[2], instId = msg[3];
			var player, instDict;
			"trying to connect instrument".postln;
			dictName.tildaGet.postln;
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
			"trying to connect pattern".postln;
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