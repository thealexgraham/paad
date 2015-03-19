(
SynthDef(\mix, { |outBus = 0, inBus1, inBus2, inBus3, gain1 = 0.5, gain2 = 0.5, gain3 = 0.5|
	var sig1, sig2, sig3;
	sig1 = In.ar(inBus1, 2);
	sig2 = In.ar(inBus2, 2);
	sig3 = In.ar(inBus3, 2);
	Out.ar(outBus, Mix.new([sig1 * gain1, sig2 * gain2, sig3 * gain3]));
}).add;

SynthDef(\mix, { |outBus = 0, inBus1, inBus2, inBus3, gain1 = 0.5, gain2 = 0.5, gain3 = 0.5|
	var sig1, sig2, sig3;
	sig1 = In.ar(inBus1, 2);
	sig2 = In.ar(inBus2, 2);
	sig3 = In.ar(inBus3, 2);
	Out.ar(outBus, Mix.new([sig1 * gain1, sig2 * gain2, sig3 * gain3]));
}).add;


SynthDef(\scale, { | outBus=0, speed = 2, gain = 0.5, pan = 0 |
	var trig, div, freq, oct, reps, env, osc, flt;
	oct = [0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3];
	trig = Impulse.kr(speed);
	env = EnvGen.kr(Env.perc(0.01, 0.1), trig, 0.5);
	freq = (Stepper.kr(trig, 0, 42, 56, 2, 42)).midicps;
	osc = Saw.ar(freq * [1, 1.01], env);
	//flt = RLPF.ar(osc, freq * 2, 0.7);
	Out.ar(outBus, Pan2.ar(osc * gain, pan));

}).add;

SynthDef(\sawWave, { | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
	var trig, env, sig;
	trig = Impulse.kr(1);
	env = EnvGen.kr(Env.perc(0.01, 0.1), trig, 0.5);
	Out.ar(outBus, Pan2.ar(Saw.ar(freq) * gain * env, pan));
}).add;

SynthDef(\sinWave, { | outBus = 0, freq = 440, pan = 0, gain = 0.5 |
	var trig, env, sig;
	trig = Impulse.kr(1);
	env = EnvGen.kr(Env.perc(0.01, 0.1), trig, 0.5);
	sig = SinOsc.ar(freq, mul: env);
	Out.ar(outBus, Pan2.ar(sig * gain, pan));
}).add;


// Just make 5 faders, 4 mix, one Master, have the 4 mix automatically connect to the master
SynthDef(\fader, { | out=0 in mute=1 amp=1 |
	Out.ar(out, In.ar(in, 2) * mute * amp)
}).add;


)
v = s.volume;
v.gui;

(
Synth(\sinWave);
)

(
~inBus1.free;
~inBus2.free;
~inBus3.free;

~group = Group.new(s);

~inBus1 = Bus.audio(s, 2);
~inBus2 = Bus.audio(s, 2);
~inBus3 = Bus.audio(s, 2);


Synth.head(~group, \sinWave, [\outBus, ~inBus1]);
Synth.head(~group, \sawWave, [\outBus, ~inBus2]);
Synth.head(~group, \scale, [\outBus, ~inBus3]);



AGHelper.makeKnobs(
	Synth.tail(~group, \mix, [\inBus1, ~inBus1, \inBus2, ~inBus2, \inBus3, ~inBus3, \outBus, 0]),
	[[\gain1, 0, 1, 0.5],
		[\gain2, 0, 1, 0.5],
		[\gain3, 0, 1, 0.5]]
);


)






(
{
    var a;
    a = [
            SinOsc.ar,
            Saw.ar(300),
            Pulse.ar(230)
        ];

    SelectX.ar(MouseX.kr(0, 1) * a.size, a) * 0.2
}.play;
)

(
{
    var a;
    a = [
            SinOsc.kr(0.25),
            LFSaw.kr(10),
            LFPulse.kr(0.3)
        ];

    SinOsc.ar(SelectX.kr(MouseX.kr(0, 1) * a.size, a) * 300 + 400) * 0.2
}.play;
)



