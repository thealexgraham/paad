(
var net = NetAddr.new("127.0.0.1", NetAddr.langPort);
net.sendMsg('/synth/add', "PanFM3OP", "1", "ratio1", 0.2, "ratio2", 1.0, "index1", 100.0, "index2", 200.0, "car_freq", 200.0, "pan", 0.0, "gain", 0.0);
net.sendMsg('/synth/add', "sawWave", "2", "freq", 440.0, "gain", 0.0, "pan", 0.0);
)