(
~vols = [0,1,0];
SynthDef(\envtest, {
	var sig, env, trig, freqy;
	freqy = 440;
	trig = Impulse.ar(1);
	env = EnvGen.ar(Env.new(~vols,[0.5,0.5]), trig);
	sig = SinOsc.ar(freqy);

	Out.ar(0, sig * env);

}).add;

)
(
~vols = [0,0.2,0];
Synth(\envtest, [\freqy, 440]);
)


(
SynthDef(\help_Env_newClear, { |out = 0, gate = 1|
    var env, envctl;
    // make an empty 4 segment envelope
    env = Env.newClear(4);
    // create a control argument array
    envctl = \env.kr(env.asArray);
    Out.ar(out, SinOsc.ar(EnvGen.kr(envctl, gate), 0) * -12.dbamp);
}).add;
)

Synth(\help_Env_newClear, [\env, Env([700,900,900,800], [0.2,0.2,0.2], \exp)]); // 3 segments

// reset then play again:
Synth(\help_Env_newClear, [ \env, Env({ rrand(60, 70).midicps } ! 4, [1,1,1], \exp)]);

// the same written as an event:
(instrument: \help_Env_newClear, env: Env({ rrand(60, 70).midicps } ! 4, [1,1,1], \exp)).play;