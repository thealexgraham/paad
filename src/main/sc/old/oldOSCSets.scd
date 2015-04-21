
//effect
		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary

	// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/effect/add/master', { arg msg;
			var effectName = msg[1];
			var id = msg[2];
			var effectDict;
			var inBus;
			"Adding Effect".postln;
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create a new dictionary for this ID

			effectName.idPut(id, Dictionary.new);
			effectDict = effectName.idGet(id);

			// Store the synth and the inBus
			effectDict.put(\synth, Synth.tail(~effectsGroup, effectName,
				[\inBus, this.getMasterIn, \outBus, 0]));

			~masterFader = effectDict.at(\synth);

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 4).asInt, 4).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				var min = item[2];
				var max = item[3];
				var param = ParameterBus.new(paramName, value, min, max);
				param.ownerId = id;
				//var bus = Bus.control.set(value);

				// Save the param bus
				effectDict.put(paramName, param);
				// Map the effect to it
				effectDict.at(\synth).map(paramName, param.bus);
			});
			// Save the inBus
			effectDict.put(\inBus, this.getMasterIn);

			("Effect added, adding effect at" + id).postln;
		});

		^("OSC Responders ready");

		this.addOSCResponder('/effect/add', { arg msg;
			var effectName = msg[1];
			var id = msg[2];
			var effectDict;
			var inBus;
			"Adding Effect".postln;
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			inBus = Bus.audio(Server.default, 2);

			// Add inBus and outBus to the default arguments
			//msg = msg.addAll(["inBus", inBus, "outBus", 0]);

			// Create a new dictionary for this ID

			effectName.idPut(id, Dictionary.new);
			effectDict = effectName.idGet(id);

			this.getMasterIn.postln;

			// Store the synth and the inBus
			effectDict.put(\synth, Synth.head(~effectsGroup, effectName,
				[\inBus, inBus, \outBus, this.getMasterIn.index]));

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 4).asInt, 4).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				var min = item[2];
				var max = item[3];
				var param = ParameterBus.new(paramName, value, min, max);
				param.ownerId = id;
				//var bus = Bus.control.set(value);

				// Save the param bus
				effectDict.put(paramName, param);
				// Map the effect to it
				effectDict.at(\synth).map(paramName, param.bus);
			});

			// Save the inBus
			effectDict.put(\inBus, inBus);

			effectDict.at(\synth).set(\outBus, this.getMasterIn);

			("Effect added, adding effect at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/effect/remove', { arg msg;
			// Free synth defs at this id
			var effectName = msg[1];
			var id = msg[2];
			var effectDict = effectName.idGet(id);

			// Free the bus
			effectDict.at(\inBus).free;
			effectDict.at(\synth).free;

			// Remove the dictionary
			effectName.idRemove(id);

			("Effect disconnected, freeing effect at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/effect/paramc', { arg msg;
			// Set float1
			var effectName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the value directly
			effectName.idGet(id).at(param).setSilent(val); // Change the value at the bus
		}, false);





// Routine player
				// Whenever an instrument is added, this will create busses for this instance of the synth
		this.addOSCResponder('/routplayer/add', { arg msg;
			var defName = msg[1];
			var id = msg[2];
			var def, player;

			def = this.getRoutPlayerDef(defName);
			def.postln;

			player = RoutinePlayer.new(id, def.at(\function), def.at(\params));
			dictName.idPut(id, player);

			// Set the current params
			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				player.setParam(paramName, value);
			});

			// test pattern for now
			player.pattern = [[1,1], [2, 0.5], [1, 0.5], [10, 1]];

			("Routine player created at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/routplayer/remove', { arg msg;
			// Free synth defs at this id
			var id = msg[1];
			dictName.idRemove(id);
			("Routine player removed at" + id).postln;
		});

		this.addOSCResponder('/routplayer/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value directly
			dictName.idGet(id).setParam(param, val); // Change the value at the bus
		});

		// CHange Functions


		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/changefunc/add', { arg msg;
			var changeFuncName = msg[1];
			var id = msg[2];
			var changeFuncDef, changeFunc;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			"Adding changeFunc".postln;
			// The rest are the defaults
			changeFuncDef = this.getDef(\changeFunc, changeFuncName);

			// Create the actual function
			changeFunc = ChangeFunc.new(changeFuncDef.at(\function), changeFuncDef.at(\params));
			changeFuncName.idPut(id, changeFunc);

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				changeFunc.setParam(paramName, value);
			});

			("changeFunc added, adding changeFunc at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/changefunc/remove', { arg msg;
			// Free synth defs at this id
			var changeFuncName = msg[1];
			var id = msg[2];
			var changeFunc = changeFuncName.idGet(id);

			("changeFunc disconnected, freeing changeFunc at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/changefunc/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value directly
			name.idGet(id).setParam(param, val); // Change the value at the bus
		});

		// INSTRUMENTS

				// Whenever an instrument is added, this will create busses for this instance of the synth
		this.addOSCResponder('/inst/add', { arg msg;
			var instName = msg[1];
			var id = msg[2];
			var instDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults

			// Create a dictionary of busses for this instrument at its ID
			instName.idPut(id, Dictionary.new);
			instDict = instName.idGet(id);

			instDict.put(\outBus, Bus.control.set(this.getMasterIn.index)); // Default out bus is 0

			// The rest of the parameters are pairs, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var param = item[0];
				var value = item[1];
				// Put the bus in and initialize it
				instDict.put(param, Bus.control.set(value));
			});

			("Inst added and busses created at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/inst/remove', { arg msg;
			// Free synth defs at this id
			var instName = msg[1];
			var id = msg[2];
			var instDict = instName.idGet(id);

			// Free the busses and remove them from this dictionary (is this necessary?)
			instDict.keysValuesArrayDo({ |key, value|
				value.free;
				instDict.removeAt(key);
			});

			("Inst disconnected, freeing busses at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/inst/paramc', { arg msg;
				// Set float1
			var instName = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the bus at param
			instName.idGet(id).at(param).set(val);

			postSilent("Changing" + instName + id + param + val);
		});


		// PATTERN GENS
				// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		this.addOSCResponder('/patterngen/add', { arg msg;
			var patternGenName = msg[1];
			var id = msg[2];
			var patternGenDef, patternGen;

			"Adding patternGen".postln;
			patternGenDef = this.getPatternGenDef(patternGenName);

			// Create the actual object
			patternGen = PatternGenerator.new(id, patternGenDef.at(\function), patternGenDef.at(\params));

			// Store the object
			patternGenName.idPut(id, patternGen);

			("patternGen added, adding patternGen at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/patterngen/remove', { arg msg;
			// Free synth defs at this id
			var patternGenName = msg[1];
			var id = msg[2];
			var patternGen = patternGenName.idGet(id);

			("patternGen disconnected, freeing patternGen at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/patterngen/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			// Set the value on the pattern object
			name.idGet(id).setParam(param, val);
		});


		// SYNTH

				// Add synth
		this.addOSCResponder('/synth/add', { |msg|
			var name = msg[1];
			var id = msg[2];
			var synthDict;

			msg.removeAt(0); // Address
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults
			this.getMasterIn.postln;
			msg = msg.addAll(["outBus", this.getMasterIn.index]);

			name.idPut(id, Dictionary.new);
			synthDict = name.idGet(id);

			// Store the synth and the inBus
			synthDict.put(\synth, Synth.head(~synthsGroup, name, msg));
			("Synth added" + id).postln;
		});

		// Remove synth
		this.addOSCResponder('/synth/remove', { |msg|
			// Free synth defs at this id
			var synthName = msg[1];
			var id = msg[2];
			var synthDict = synthName.idGet(id);

			synthDict.at(\synth).free;
			synthName.nameGet.removeAt(id);

			("Inst disconnected, freeing busses at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/synth/paramc', { arg msg;
				// Set float1
			var synthName = msg[1], param = msg[2], id = msg[3], val = msg[4];

			// Set the bus at param
			synthName.idGet(id).at(\synth).set(param, val);

			postSilent("Changing" + synthName + id + param + val);
		});


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
		