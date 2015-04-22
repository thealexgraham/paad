+ JavaHelper {

	newInstrument { |name, function, params|
		this.putDef(\instrument, name, (function: function, params: params));

		this.sendSilentMsg("/def/ready/"++name, 1); // Should this go somewhere else?

	}

	createInstListeners {
		~instrumentGroup = Group.head(Server.default);

		this.addOSCResponder('/inst/connect/effect', { arg msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var inst, effectObj;

			// Get the dictionaries
			inst = this.idGet(instName, instId);
			effectObj = this.idGet(effectName, effectId);

			// Set the outBus's control bus to effect inBus index
			inst.paramAt(\outBus).bus.set(effectObj.inBus.index);

			("Connected instrument to effect").postln;
		});

		this.addOSCResponder('/inst/disconnect/effect', { arg msg;
			var instName = msg[1], instId = msg[2], effectName = msg[3], effectId = msg[4];
			var inst, effectObj;

			inst = this.idGet(instName, instId);
			effectObj = this.idGet(effectName, effectId);

			// Change instrument's output bus back to default (0)
			inst.paramAt(\outBus).bus.set(this.getMasterIn.index);
			("Disconnected instrument from effect").postln;
		});
	}
}