(
b = Bus.control(s);
a = Bus.audio(s, 1);
a.postln;
b.value = a.index;
b.get({arg value;
	value.postln;
	"4".postln;
});

)


// get a bus
b = Bus.control(s);

// map the synth's second input (ffreq) to read
// from the bus' output index
x.map(1, b);

// By setting the bus' value you send a /c_fill message
// to each channel of the bus setting it to supplied float value
b.value = 100;
b.value = 1000;
b.value = 30;

// Since this is a single channel bus this has the same effect
b.set(300);
b.numChannels.postln;

// multi-channel:  b.set(300,350);
// Get the current value. This is asynchronous so you can't rely on it happening immediately.
(
a = "waiting";
b.get({arg value; a = value; ("after the server responds a is set to:" + a).postln;});
("a is now:" + a).postln;
)