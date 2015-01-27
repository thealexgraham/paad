
// Example of creating and embedding pbinds on the fly, with ability to send messages when it happens
(

~chooseFun = {
	"Choosing".postln;
	Array.fill(5, { 10.rand });
};

~array = [0, 0, 7, 0, 0];
e = Prout({| ev |
	var pat, refPat, count = 0;

	loop {
		refPat = Pbind(*[dur: 1, note: Pseq(~array)]);
		ev = refPat.embedInStream(ev);
		("Reference pattern played").postln;

		pat = Pbind(*[
			dur: [0.2, 0.4].choose,
			note: Pseq(~chooseFun.value, 1 )
		]);
		ev = pat.embedInStream(ev);
		count = count + 1;
		("Loop is done"+count).postln;

	}

}).play;
f = e.asStream;
//50.do({ f.asStream.next(Event.new).postln; });

)

e.pause

~array = [0, 1, 1, 1];
~array = [7, 7, 7, 7];

// Lots of busses does not affect speed
(
3000.do({
	Bus.control(s,1);
});
	)
Bus.control(s,1);


// Bus SynthDef example
(
// make two control rate busses and set their values to 880 and 884.
b = Bus.control(s, 1); b.set(880);
c = Bus.control(s, 1); c.set(884);
// and make a synth with two frequency arguments
x = SynthDef("tutorial-map", { arg freq1 = 440, freq2 = 440;
    Out.ar(0, SinOsc.ar([freq1, freq2], 0, 0.1));
}).play(s);
)
// Now map freq1 and freq2 to read from the two busses
x.map(\freq1, b, \freq2, c);

// Now make a Synth to write to the one of the busses
y = {Out.kr(b, SinOsc.kr(1, 0, 50, 880))}.play(addAction: \addToHead);

// free y, and b holds its last value
y.free;

// use Bus-get to see what the value is. Watch the post window
b.get({ arg val; val.postln; f = val; });

// set the freq2, this 'unmaps' it from c
x.set(\freq2, f / 2);

// freq2 is no longer mapped, so setting c to a different value has no effect
c.set(200);

x.free; b.free; c.free;



/*
Play Box, attach a synth to it, and hit play.
It plays the pattern attached to it
Synth should be controlled by busses


Instrument:
Take in min/max/whatever params
When an instrument instance is hooked up, create busses for each param
Have the GUI control the busses

When instrument is delete, free the busses

Hook up something like an LFO to a bus, drag and drop that in as a control synthdef
- Connect it to change the Bus output to the Bus of the instrument

If a bus is being controlled by a synthdef, we should also know about it in the GUI

What happens if a Control Synth is hooked into a regular synth control?
Should all arguments of all synth defs have a bus?
Or when a Control Synth is hooked up to a parameter, it could make it into a bus... probably not
*/


(
~lfoBus.free;
~sawBus.free;
~freqbus.free;
~ampBus.free;

~lfoBus = Bus.control(s,1);
~freqBus = Bus.control(s,1);
~sawBus = Bus.control(s,1);
~ampBus = Bus.control(s,1);

SynthDef(\simpleLFO, { |bus, rate = 1, amount = 1 |
	var sig = SinOsc.kr(rate, 0, 0.5, 1) * amount;
	Out.kr(bus, sig);
}).add;

SynthDef(\sawsin, { | outBus = 0, freqAdd = 0, freq = 0, lfo = 0, saw = 0, gate = 1, amp = 1 |
	var sig, env;
	env = EnvGen.kr(Env.adsr(0.01, 0.3, 0.5, 0.01), gate, doneAction:2);
	sig = SinOsc.ar(freq + (freqAdd * 200));
	sig = sig + (Saw.ar(freq + (freqAdd * 200)) * saw);
	sig = sig * env * SinOsc.ar(lfo,0,0.5,1) * amp;
	Out.ar(outBus, [sig, sig]);
}).add;

)


(
var syn;

x = Synth.new(\sawsin, [\freq, 440, \lfo, 1]);

x.map(\lfo, ~lfoBus, \saw, ~sawBus, \amp, ~ampBus);
)

(
~sawBus.set(0);
~lfoBus.set(0);
~freqBus.set(0);
~ampBus.set(0.5);
~a1 = [[5, 2], [8, 2], [10, 2], [8, 2], [\rest, 16]];

~patt = Pbind(\instrument, \sawsin,
	\legato, 0.9,
	\saw, ~sawBus.asMap,
	\amp, ~ampBus.asMap,
	\freqAdd, ~freqBus.asMap,
	\out, 0);

~rests = Pbindf(~patt, #[\note, \dur], [[\rest, 4]]);

f = Pseq([
	Pbindf(~patt,
		//\amp, 0.2,
		#[\note, \dur], Pseq(~a1, 5),
		\octave, 4,
)]).play;

)

f.pause
(
~lfoSynth = Synth.new(\simpleLFO, [\bus, ~lfoBus]);

~sawMap = {Out.kr(~sawBus, In.kr(~lfoBus))}.play;
~freqMap = {Out.kr(~freqBus, In.kr(~lfoBus))}.play;
)

~sawBus.scope
~freqBus.scope

(
Synth.new(\simpleLFO, [\bus, ~sawBus]);
Synth.new(\simpleLFO, [\bus, ~freqBus]);
)

~lfoSynth.set(\bus, ~sawBus);

~sawBus.get({ arg val; val.postln; f = val; });

(
//~sawBus.set({[0, 1, 3, 4].choose}.value);
~sawBus.get({ arg val; val.postln; f = val; });
~freqBus.get({ arg val; val.postln; f = val; });
//~lfoBus.set(1);
)