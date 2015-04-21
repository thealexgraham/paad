(
// read a whole sound into memory
s = Server.local;
// note: not *that* columbia, the first one
b = Buffer.read(s, Platform.resourceDir +/+ "sounds/tom.wav");
x = Bus.control.set(b);
f = Synth(\bufPlayer);
f.map(\buff, x);
)

(
b = Buffer.read(s, Platform.resourceDir +/+ "sounds/rhodes.aif");
x.set(b.bufnum);
)
)
(
b = Buffer.read(s, Platform.resourceDir +/+ "sounds/synth.wav");
)

{ BufRd.ar(1, b, LFSaw.ar(BufDur.ir(b).reciprocal).range(0, BufFrames.ir(b)) ) }.play;

(
f = {
	var choices = [];
	var files = [ "C:/Program Files (x86)/SuperCollider-3.6.6/sounds/rhodes.aif",
		"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/synth.wav",
		"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/tom.wav",
		"C:/Program Files (x86)/SuperCollider-3.6.6/sounds/warm.wav" ];

	files.do({|item, i|
		var buffer = Buffer.read(s, item);
		var fileName = PathName.new(item).fileName;
		choices = choices.add([fileName, buffer.bufnum]);
	});

	choices.postln;
};
f.value;
)

(
x = Bus.control;
f = Synth(\bufPlayer);

)
(
f.map(\buff, x);
)

x.set(2)

(
SynthDef(\bufPlayer, {
	| otuBus, buff, pan = 0, gain = 1 |
	var sig = BufRd.ar(1, buff, Phasor.ar(0, BufRateScale.kr(buff), 0, BufFrames.kr(buff)));
	Out.ar(0, Pan2.ar(sig, pan) * gain);
}).add;
)


(
(
Dialog.openPanel({ arg path;
    path.postln;
},{
    "cancelled".postln;
}, true);
)
)
)