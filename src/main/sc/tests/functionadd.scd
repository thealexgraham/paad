(
a = { | test |
	("whatever to " + test).postln;
};

)

(
OSCresponder(nil, "/testing", { arg time, resp, msg;
	"Testing".postln;
}).add.oneShot.verify;

)


(
OSCdef(\test, {|msg|
	"The function happened".postln;

	"The function finished".postln;

}, '/testing4').verify(1259).oneShot;

// n = NetAddr.new("127.0.0.1", NetAddr.langPort);
// n.sendMsg("/testing4", 1);

)

(
OSCFunc(path: \tester, {|msg|
	"The function happened".postln;

	"The function finished".postln;

}).verify(1260).oneShot;

// n = NetAddr.new("127.0.0.1", NetAddr.langPort);
// n.sendMsg("/testing4", 1);

)

(


)




a.value("Test");
a.def.sourceCode.
d = a.def.sourceCode.interpret
d.value("test");
d

r = a.def.sourceCode;
r
r.removeAt(r.size - 1)

(
x = { |function, addCode|
	var code, newCode;
	code = function.def.sourceCode;
	newCode = code.copyRange(0, code.size-2) ++ addCode ++ "}";
	newCode.postln;
	newCode.interpret; // return new function

}

)




d = x.value(a, "\"I added this\".postln;");
d.value("test");

a.def.sourceCode.copyRange(0,r.size-2)

d = (a.def.sourceCode.copyRange(0,r.size-2) ++ "\"I added this\".postln;").interpret;
d.value("test");
Array

List[1, 2, ["comment", [3, 2]], { 1.0.rand }].postcs;
FunctionDef

f = Function.new("{ |test| test.postln;}");

