f = { |test|
	test.postln;
	5 + 4;
	\test;
	test;
};


(
q = 	[[\lofreq, 0, 600, 80],
		[\band1, -80, 80, 0],
		[\midfreq, 200, 1000, 800],
		[\band2, -80, 80, 0],
		[\hifreq, 800, 5000, 2000],
		[\band3, -80, 80, 0],
		[\mix, -1, 0, 1]];
g = {| arr ... other |
	var endString = "[";
	arr.do({|item, i|
		endString = endString++item.asString++"\n";
	});
	endString++"]";
	other.postln;
};

g.value(q);
)

n = NetAddr.new("127.0.0.1", 1253);

n.sendMsg("/testmessage", [4, 5, 6]);
n.sendMsg("/testmessage", f.def.sourceCode);
n.sendMsg("/testmessage", g.value(q));
n.sendMsg("/testmessage", 'test');
f.betterSend;

(
f = (

	betterSend: { |msg|
		if(msg.isString
		n.sendMsg("/testmessage", "test")
	},
)
)