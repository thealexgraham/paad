/*+ OSCresponder {
	*respond { arg time, addr, msg;
		var cmdName, verify = false, hit = false, oldAction;
		#cmdName = msg;
		if (cmdName.asString.beginsWith("/verify"),
			{
				verify = true;
				cmdName = cmdName.asString.replace("/verify","").asSymbol;
			}
		);
		all.do { |resp|
			if(
				(resp.cmdName === cmdName)
				or:
				{ resp.cmdNameWithoutSlash === cmdName }
				and: { addr.matches(resp.addr) }
			) {
				if (verify, {
					oldAction = resp.verify(cmdName, addr);
				});
				resp.value(time, msg, addr);
				resp.action_(oldAction);
				hit = true;
			};
		};
		^hit
	}

	verify { |cmd, addr|
		var verifyFunc, wrappedFunc;
		wrappedFunc = action;
		verifyFunc = {
			arg ...args;
			//var net = NetAddr.new("127.0.0.1", port);
			wrappedFunc.value(*args);
			addr.sendMsg(cmd++"/done", 1);
			("Sent message to "++cmd++"/done").postln;
		};
		this.action_(verifyFunc);
		^wrappedFunc;
	}

}*/