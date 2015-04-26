+ JavaHelper {

	newModule { |type, id, name|
		var def = this.getDef(type, name);

		var function = def.at(\function);
		var params = def.at(\params);

		switch(type,
			\effect, { ^EffectModule.new(id, name, function, params) },
			\synth, { ^SynthModule.new(id, name, function, params) },
			\changeFunc, { ^ChangeFunc.new(id, name, function, params) },
			\instrument, { ^InstrumentModule.new(id, name, function, params) },
			\patternGen, { ^PatternGenerator.new(id, name, function, params) },
			\taskRunner, { ^TaskRunner.new(id, name, function, params) },
			\taskPlayer, { ^TaskPlayer.new(id, name, function, params) },
			\patternPlayer, { ^PatternPlayer.new(id, name, function, params) },
			{^nil}
		);
	}

	createModuleListeners {
		var defaultParams;

		this.addOSCResponder('/module/add', { arg msg;
			var type = msg[1].asSymbol;
			var name = msg[2];
			var id = msg[3];
			var def, module;

			msg.removeAt(0); // Address
			msg.removeAt(0); // Type
			msg.removeAt(0); // SynthName
			msg.removeAt(0); // ID

			// The rest are the defaults
			def = this.getDef(type, name);

			// Create the actual function
			module = this.newModule(type, id, name);
			name.idPut(id, module);

			// The rest of the parameters are quads, reshape so we can use them
			msg.reshape((msg.size / 2).asInt, 2).do({ |item, i|
				var paramName = item[0];
				var value = item[1];
				module.setParam(paramName, value);
			});
			"BLAH BLAH".postln;
			("Module added at" + id).postln;
		});

		// Whenever the plugin is removed (or killed internally) this will free the synth
		this.addOSCResponder('/module/remove', { arg msg;
			// Free synth defs at this id
			var name = msg[1];
			var id = msg[2];
			var module = name.idGet(id);

			module.removeSelf;
			("changeFunc disconnected, freeing changeFunc at" + id).postln;
		});

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/module/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			"receiving paramc".postln;
			name.idGet(id).setParam(param, val); // Change the value at the bus
		}, false);

		this.addOSCResponder('/module/live/paramc', { arg msg;
			// Set float1
			var name = msg[1], param = msg[2], id = msg[3], val = msg[4];
			msg.postln;
			"Got live message".postln;
			name.idGet(id).setParamLive(param, val); // Change the value at the bus
		}, false);

		// [/synth/newparam, synthName, paramName, id, value]
		this.addOSCResponder('/module/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], action = msg[3];
			name.idGet(id).doAction(action.asSymbol);
		}, false);

		// New Functions
		this.addOSCResponder('/module/connect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.addListener(targetObj, action.asSymbol);

		});

		this.addOSCResponder('/module/disconnect/action', { arg msg;
			// Set float1
			var name = msg[1], id = msg[2], targetName = msg[3], targetId = msg[4], action = msg[5];
			var player, targetObj;

			player = name.idGet(id);
			targetObj = targetName.idGet(targetId);
			player.removeListener(targetObj, action.asSymbol);
		});

	}

	newDef { |defName, type, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		var message = ["/def/add/full", defName, type];
		// message.postln;
		message = message.add(function.def.sourceCode);
		params.do({ |item, i|
			var param = item[0].asString;
			if((item[1].isNumber != true), { // Type is specified
				var paramType = item[1];
				switch(paramType,
					\int, {
						var min = item[2];
						var max = item[3];
						var default = item[4];
						message = message.addAll([param, paramType, min, max, default]);
					},
					\float, {
						var min = item[2];
						var max = item[3];
						var default = item[4];
						message = message.addAll([param, paramType, min, max, default]);
					},
					\choice, {
						var choiceName = item[2];
						var choiceValue = item[3]; // Assume array
						var choiceType = item[4];
						//var message = choiceValue.insertAll(0, "/patterngendef/param", param, type, choiceName);
						message = message.addAll([param, paramType,
							choiceName, choiceValue.asString, choiceType.preserveKeywords.asString]); //choiceValue.size].addAll(choiceValue));
					}, //default
					\return, {
						var returnType = item[2];
						message = message.addAll([param, paramType, returnType.preserveKeywords.asString]);
					},
					{
						// If the type doesn't match the choices, just send
						var min = item[2];
						var max = item[3];
						var default = item[4];
						message = message.addAll([param, paramType, min, max, default]);
					}
				);
				}, { // Type isn't specified, send as float
					var min = item[1];
					var max = item[2];
					var default = item[3];
					message = message.addAll([param, \float, min, max, default]);
				}
			);
		});

/*		if (java = false,
			{ net.sendBundle(0, message); },
			{ this.sendDefVerify(message); }
		);*/

		this.sendDefVerify(message);
		//

		^("Definition Added");
	}

	newDefSeperate { |defName, type, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr

		net.sendMsg("/def/add", defName, type);

		// Wait until Java is ready to receive the rest
		OSCdef((defName++"/ready").asSymbol, { |msg, time, addr|
			// Should we wait for callback?
			params.do({ |item, i|
				var param = item[0].asString;
				if((item[1].isNumber != true), { // Type is specified
					var paramType = item[1];
					switch(paramType,
						\int, {
							var min = item[2];
							var max = item[3];
							var default = item[4];
							net.sendMsg("/def/param", defName, param, paramType, min, max, default);
						},
						\float, {
							var min = item[2];
							var max = item[3];
							var default = item[4];
							net.sendMsg("/def/param", defName, param, paramType, min, max, default);
						},
						\choice, {
							var choiceName = item[2];
							var choiceValue = item[3]; // Assume array
							//var message = choiceValue.insertAll(0, "/patterngendef/param", param, type, choiceName);
							var message = ["/def/param", defName, param, paramType, choiceName].addAll(choiceValue);
							net.sendBundle(0, message);
						}, //default
						{
							// If the type doesn't match the choices, just send
							var min = item[2];
							var max = item[3];
							var default = item[4];
							net.sendMsg("/def/param", defName, param, paramType, min, max, default);
						}
					);
					}, { // Type isn't specified, send as float
						var min = item[1];
						var max = item[2];
						var default = item[3];
						net.sendMsg("/def/param/default", defName, param, min, max, default);
					}
				);
			});
			net.sendMsg("/def/func", defName, function.def.sourceCode);
		}, ("/"++defName++"/ready").asSymbol, recvPort:NetAddr.langPort).oneShot;

		^("Definition Added");
	}

}