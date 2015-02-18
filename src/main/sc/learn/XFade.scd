(
SynthDef("help-SinOsc", { arg freq=440, out;
    Out.ar(out, SinOsc.ar(freq, 0, 0.1))
}).send(s);

SynthDef("help-XOut", { arg out=0, xFade=1;
    var source;
        source = PinkNoise.ar(0.05);

        // write to the bus, crossfading with previous contents
        XOut.ar(out, xFade, source);

}).send(s);
)

Synth("help-SinOsc", [\freq, 500]);
a = Synth.tail(s, "help-XOut");


a.set(\xFade, 0.7);
a.set(\xFade, 0.4);
a.set(\xFade, 0.0);