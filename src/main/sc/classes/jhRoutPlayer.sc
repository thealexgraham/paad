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
		OSCresponder(nil, "/routplayer/add", { arg time, resp, msg;
			var id = msg[1];
			var player = RoutinePlayer.new;
			var chooser = PatternChooser.new;
			// test pattern for now
			player.pattern = [[1,1], [2, 0.5], [1, 0.5], [10, 1]];

			chooser.addPattern([[1,1], [2, 0.5], [1, 0.5]], 50);
			chooser.addPattern([[5,1], [5, 0.5], [5, 0.5]], 50);
			chooser.addPattern([[10,1], [5, 0.5], [10, 0.5],[10, 0.2],[10, 0.7]], 25);
			player.connectPatternObject(chooser);

			dictName.nameGet.put(id, player);
			("Routine player created at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/routplayer/remove", { arg time, resp, msg;
			// Free synth defs at this id
			var id = msg[1];
			dictName.nameGet.removeAt(id);
			("Routine player removed at" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/routplayer/play", { arg time, resp, msg;
				// Set float1
			var id = msg[1];
			var player = dictName.idGet(id);
			player.play;
			("Trying to play routine player" + id).postln;
		}).add;

		// [/synth/newparam, synthName, paramName, id, value]
		OSCresponder(nil,"/routplayer/stop", { arg time, resp, msg;
				// Set float1
			var id = msg[1];
			var player = dictName.idGet(id);
			player.stop;
			("Trying to stop routine player" + id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/connect/inst", { arg time, resp, msg;
			// Set float1
			var id = msg[1], instName = msg[2], instId = msg[3];
			var player, instDict;
			"trying to connect instrument".postln;
			dictName.tildaGet.postln;
			player = dictName.idGet(id);
			instDict = instName.idGet(instId);
			player.connectInstrument(instName, instDict);

			("Connected instrument" + instName + "to player at ID"+id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/remove/inst", { arg time, resp, msg;
			// Set float1
			var id = msg[1];
			var player, instDict;
			player = dictName.idGet(id);
			player.removeInstrument();
			("Removed instrument").postln;
		}).add;

		OSCresponder(nil,"/routplayer/connect/pattern", { arg time, resp, msg;
			// Set float1
			var id = msg[1], patternName = msg[2], patternId = msg[3];
			var player, patternObj;
			"trying to connect pattern".postln;
			player = dictName.idGet(id);
			patternObj = patternName.idGet(patternId);
			player.connectPatternObject(patternObj);

			("Connected Pattern Object to player at ID"+id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/remove/pattern", { arg time, resp, msg;
			// Set float1
			var id = msg[1];
			var player, instDict;
			player = dictName.idGet(id);
			player.removeInstrument();
			("Removed instrument").postln;
		}).add;

		OSCresponder(nil,"/routplayer/connect/playaction", { arg time, resp, msg;
			// Set float1
			var id = msg[1], targetName = msg[2], targetId = msg[3];
			var player, targetObj;

			player = dictName.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.addListener(targetObj);

			("Connected Player Object to player at ID"+id).postln;
		}).add;

		OSCresponder(nil,"/routplayer/remove/playaction", { arg time, resp, msg;
			// Set float1
			var id = msg[1], targetName = msg[2], targetId = msg[3];
			var player, targetObj;

			player = dictName.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.removeListener(targetObj);
			("Removed instrument").postln;
		}).add;

	}

}