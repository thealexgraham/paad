+ JavaHelper {
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
							var choiceName = item[2][0];
							var choiceValue = item[2][1]; // Assume array
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


	newDef { |defName, type, function, params|
		var net = NetAddr.new("127.0.0.1", this.sendPort);    // create the NetAddr
		var message = ["/def/add/full", defName, type];
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
						var choiceName = item[2][0];
						var choiceValue = item[2][1]; // Assume array
						//var message = choiceValue.insertAll(0, "/patterngendef/param", param, type, choiceName);
						message = message.addAll([param, paramType, choiceName, choiceValue.size].addAll(choiceValue));
					}, //default
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

		net.sendBundle(0, message);
		^("Definition Added");
	}

}