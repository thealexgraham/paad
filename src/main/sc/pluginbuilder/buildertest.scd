// Alex Graham
// PluginBuilder example code

// Remember to go to your pluginbuilder folder and run "build-plugin.bat" between
// each "PluginBuilder.generateCode()"

// If you are running this code from somewhere other than your pluginbuilder directory,
// use the third argument in generateCode() to specify where your plugin builder root
// folder is.

(
SynthDef(\FM3OP, { | car_freq, mod_ratio1, mod_ratio2, mod_index1, mod_index2, amp = 0.3 |
    var fm = SinOsc.ar(car_freq +
        (SinOsc.ar(car_freq*mod_ratio1 +
            (SinOsc.ar(car_freq*mod_ratio2) * mod_index2)) * mod_index1));
    //var env = EnvGen.kr(Env.linen, doneAction: 2);
	Out.ar(0, Pan2.ar(fm * amp, 0));
}).add;

// [name, min, max, default]
~params = [[\mod_ratio1, 0, 5, 0.2],
	[\mod_ratio2, 0, 5, 1],
	[\mod_index1, 0, 1000, 100],
	[\mod_index2, 0, 1000, 200],
	[\car_freq, 100, 1000, 200],
	[\amp, 0, 1, 0]];

[\instance_name, \instance_type, \instance_id, \mod_ratio1, 0, 5, 0.2]

PluginBuilder.generateCode("FM3OP", ~params);
PluginBuilder.createListeners("FM3OP", ~params);
)


(
~params = [[\intensity, 0, 1, 0.5],
	[\amp, 0, 1, 0]];

SynthDef(\wind, { |intensity, amp|
	var ampEnv, multEnv, windGen;
	//multEnv = EnvGen.ar(Env.new([1,0.5],[2]));
	ampEnv = EnvGen.ar(Env.new([0,1], [1]));
	windGen = RLPF.ar(WhiteNoise.ar(), abs(SinOsc.ar(0.1, 0, 1000 * intensity, 600)), 1) * amp * (intensity * 0.9) * ampEnv;
	Out.ar(0, Pan2.ar(windGen, SinOsc.ar(0.1,0, 0.2)));
}).add;

PluginBuilder.generateCode("wind", ~params);
PluginBuilder.createListeners("wind", ~params);
)