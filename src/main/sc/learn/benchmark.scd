n = NetAddr("127.0.0.1", 57120); // local machine

OSCdef(\test, {|msg, time, addr, recvPort| \unmatching.postln}, '/chat', n); // def style
OSCdef.newMatching(\test2, {|msg, time, addr, recvPort| \matching.postln}, '/chat', n); // path matching
OSCdef(\test3, {|msg, time, addr, recvPort| \oneShot.postln}, '/chat', n).oneShot; // once only
OSCdef.newMatching(\test3, {|msg, time, addr, recvPort| \asdfsadfs.postln}, '/', n); // path matching

m = NetAddr("127.0.0.1", 57120); // loopback

m.sendMsg("/chat", "Hello App 1");
m.sendMsg("/chat", "Hello App 1"); // oneshot gone
m.sendMsg("/s", "Hello App 1");
m.sendMsg("/*", "Hello App 1");
m.sendMsg("/chit", "Hello App 1"); // nothing


// scanFor is fairly efficient. compare the following benchmarks:

// comparison of array indexing and identity set lookup
(
f = { |n=4|
    var x, y, i;
    x = (0..n);
    y = x.as(IdentitySet);
    i = x.choose;
    bench { 100.do { x[i] } };
    bench { 100.do { y.scanFor(i) } };
}
);

f.(4)
f.(8)
f.(100)
f.(10000)