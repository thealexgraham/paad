
// OSC Responders
(

n = NetAddr("127.0.0.1", 57120); // local machine

~wind = Dictionary.new;
~rain = Dictionary.new;
~thunder = Dictionary.new;

~all = [~wind, ~rain, ~thunder]; //, ~rain, ~thunder];

r = OSCresponder(nil, '/fmod/start', { arg time, resp, msg;
	var id = msg[1];
	// Create synth defs at this location
	~wind.put(id, Synth.new(\wind, [\intensity, 0, \amp, 0]));
	~rain.put(id, Synth.new(\rain, [\intensity, 0, \amp, 0]));
	~thunder.put(id, Synth.new(\thunder, [\intensity, 0, \amp, 0]));
	("Plugin connected, adding synths at" + id).postln;
}).add;

r = OSCresponder(nil, '/fmod/dying', { arg time, resp, msg;
	// Free synth defs at this id
	var id = msg[1];
	~wind[id].free;
	~rain[id].free;
	~thunder[id].free;
	("Plugin disconnected, freeing synths at" + id).postln;
}).add;

// Wind
r = OSCresponder(nil, '/fmod/float1', { arg time, resp, msg;
	// Set float1
	var id = msg[1], val = msg[2];
	~wind[id].set(\intensity, val);
}).add;

// Rain
r = OSCresponder(nil, '/fmod/float2', { arg time, resp, msg;
	var id = msg[1], val = msg[2];
	~rain[id].set(\intensity, val);
}).add;

// Thunder
r = OSCresponder(nil, '/fmod/float3', { arg time, resp, msg;
	var id = msg[1], val = msg[2];
	~thunder[id].set(\intensity, val);
}).add;

// Gain
r = OSCresponder(nil, '/fmod/volume', { arg time, resp, msg;
	var id = msg[1], val = msg[2];

	~all.do({ |item, i|
		item[id].set(\amp, val);
	});
	("Adjusting volume for" + id + "to" + val).postln;
}).add;

// r = OSCresponder(nil,
// 	'/fmod/test', { arg time, resp, msg;
// 		[msg].postln;
// }).add;

)

// Synth Defs

(
SynthDef(\wind, { |intensity, amp|
	var ampEnv, multEnv, windGen;
	//multEnv = EnvGen.ar(Env.new([1,0.5],[2]));
	ampEnv = EnvGen.ar(Env.new([0,1], [1]));
	windGen = RLPF.ar(WhiteNoise.ar(), abs(SinOsc.ar(0.1, 0, 1000 * intensity, 600)), 1) * amp * (intensity * 0.9) * ampEnv;
	Out.ar(0, Pan2.ar(windGen, SinOsc.ar(0.1,0, 0.2)));
}).add;

SynthDef(\rain, { |intensity, amp|
	var rain = Limiter.ar(
		tanh(
			3 * GVerb.ar(
				HPF.ar(
					in: PinkNoise.ar(mul: (intensity * 0.1 + 0.01) + LFNoise1.kr(0.1, 0.02)) +
					LPF.ar(
						in: Dust2.ar(LFNoise1.kr(0.2).range(40,50)),
						freq: 1500),
					freq: 400
                ),
                roomsize: 250,
				revtime: 100,
				damping: 0.25,
				drylevel:0.3
            ) * intensity //Line.kr(0,1,10)
        )
    );
	Out.ar(0, Pan2.ar(amp * rain, 0));
}).add;

SynthDef(\thunder, { |intensity, amp|
	var trig1, trig2, thunder;

	trig1=Dust.kr(0.3 * intensity);//slow trigger for each thunder, controlled by global envelope
	///trig1 = Line.kr(1, 0, 0.1);
	trig2=Dust.kr(15);

	thunder = FreeVerb.ar(
			Pan2.ar(
				in: RLPF.ar(						//filter, in, freq, rq, mul, add
					WhiteNoise.ar(1),		//white noise is the basis
					800 *						//maximum frequency
					EnvGen.kr(				//how one thunder goes
						Env.perc(
							attackTime: 0.05,
							releaseTime: 7,
							level: 0.5,
							curve: \sine), //attack, release, peak, curve
						trig1					//slow trigger
					) * intensity + 20, // bad things happen when frequency = 0
					0.55,						// reciprocal Q
					EnvGen.kr(				//rumbling, controls amplitude
						Env.perc(
							attackTime: 0.01,
							releaseTime: 0.5,
							level: 2,
							curve: -1),
						gate: trig2					//fast trigger
					)							// end of for amplitude
				),								//end of LPF
				pos: LFNoise1.kr(0.1)			//freq
			) * 1.9,									//end of Pan2
			mix: 0.5,								//mix
			room: 0.8,								//room
			damp: 0.6									//damp
		);

				//fast trigger for rumbling
	Out.ar(0, thunder * amp);										//einde FreeVerb
}).add;

)
