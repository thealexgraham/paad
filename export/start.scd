(
var net = NetAddr.new("127.0.0.1", NetAddr.langPort);
net.sendMsg('/effect/add/master', "fader", "1", "amp", 0.19, 0.0, 1.0);
net.sendMsg('/synth/add', "sawWave", "6", "freq", 440.0, "gain", 0.06, "pan", 0.0);
net.sendMsg('/effect/connect/effect', "fader", "2", "fader", "1");
net.sendMsg('/effect/connect/effect', "fader", "3", "fader", "1");
net.sendMsg('/effect/connect/effect', "fader", "4", "fader", "1");
net.sendMsg('/effect/connect/effect', "fader", "5", "fader", "1");
)