+ OSCFunc {
	*newVerify { arg func, path, srcID, recvPort, argTemplate, dispatcher;
		var verifyFunc, wrappedFunc;
		wrappedFunc = func;
		verifyFunc = {
			arg ...args;
			wrappedFunc.value(*args);
			path.postln;
		};
		path.postln;
		^super.new.init(verifyFunc, path, srcID, recvPort, argTemplate, dispatcher ? defaultDispatcher);
	}


	addToFunction { |function, addCode|
		var code, newCode;
		code = function.def.sourceCode;
		newCode = code.copyRange(0, code.size-2) ++ addCode ++ "}";
		newCode.postln;
		newCode.interpret; // return new function

	}

	verify { |port|
		var oneShotFunc, wrappedFunc;
		wrappedFunc = func;
		oneShotFunc = {
			arg ...args;
			var net = NetAddr.new("127.0.0.1", port);
			// If the first thing you see is verify, remove it, and send the verificaiton msg at the end
			wrappedFunc.value(*args);
			net.sendMsg(path++"/done", 1);
			("Sent message to "++path++"/done").postln;
		};
		this.prFunc_(oneShotFunc);
	}

}