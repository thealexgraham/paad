(
r = Routine { arg inval;
    loop {
		inval.postln;
		    1.0.yield;
		    inval.postln;
	}
};

r = Routine { arg inval;
    loop {
		inval.postln;
		1.0.wait;
		"test".postln;
	}
};

t = Task({ | arg1 arg2|
	loop {
		"Test".postln;
		1.0.wait;
	}
}.valueEnvir);
)

(
t.start;

r.play;
r.value("hello routine");
r.value("goodbye routine");
)


(
var args = [\arg1, 50, \arg2, 500];
f = { |arg1, arg2|
	loop {
		arg1.postln;
		arg2.postln;
		1.wait;
	};
};

t = Task({
	f.performKeyValuePairs(\value, args);
});

t.play;
)

/*f.def.argNames;

f = {
	f.def.argNames.do({ |item, i|
		item.envirPut(args.at(item));
		*/

)