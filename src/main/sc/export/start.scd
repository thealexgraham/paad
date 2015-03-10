(
var net = NetAddr.new("127.0.0.1", NetAddr.langPort);
net.sendMsg('/synth/add', "sineWave", "2", "freq", 440.0, "gain", 0.06, "pan", 0.0);
net.sendMsg('/effect/add', "verb", "3", "verbMix", 0.82, 0.0, 1.0, "room", 1.0, 0.0, 1.0, "damp", 1.0, 0.0, 1.0);
net.sendMsg('/synth/add', "scale", "5", "speed", 2.0, "gain", 0.41, "pan", 0.0);
net.sendMsg('/effect/add', "verb", "6", "verbMix", 1.0, 0.0, 1.0, "room", 1.0, 0.0, 1.0, "damp", 1.0, 0.0, 1.0);
net.sendMsg('/effect/add', "feedbackdelay", "7", "delaytime", 2.34, 0.0, 10.0, "feedback", 0.89, 0.0, 1.0);
net.sendMsg('/synth/connect/effect', "sineWave", "2", "verb", "3");
net.sendMsg('/synth/connect/effect', "scale", "5", "verb", "6");
net.sendMsg('/effect/connect/effect', "verb", "6", "feedbackdelay", "7");
)