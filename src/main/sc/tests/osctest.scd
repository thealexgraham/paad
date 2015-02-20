(
n = NetAddr.new("127.0.0.1", NetAddr.langPort);
OSCdef(\test, { |msg|
	msg.postln;
}, '/mytest');
)

p = ["/mytest", 0, 1, 2];
n.sendMsg("/mytest", 0, 1, 2);
n.sendMsg(["mytest", 0, 1, 2]);
n.sendMsg.valueArray(p);
n.sendMsg(p);
n.sendBundle(0, p);
n.sendMsg(0, "/mytest", 0, 1, 2);
n.sendRaw(p);

(

)

n = NetAddr("127.0.0.1", 57120); // 57120 is sclang default port
r = OSCFunc({ arg msg, time; [time, msg].postln }, '/good/news', n);

n.sendMsg("/good/news", "you", "not you");
n.sendMsg("/good/news", 1, 1.3, 77);


n.sendBundle(0.2, ["/good/news", 1, 1.3, 77]);

r.free;
n.disconnect;