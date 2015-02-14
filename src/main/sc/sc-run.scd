
~java.class.postln;
(
(thisProcess.nowExecutingPath.dirname ++ "/startup/*").pathMatch.do({
	|item, i|
	item.load;
});
"done";
)

(
)

(
"server ready".postln;

// Send java the port sclang is listening on
NetAddr.langPort.postln;
javaCommand("receivePort", NetAddr.langPort);

// Create Network
n = NetAddr("127.0.0.1", NetAddr.langPort);

// Create a new java helper at the default port
~java = JavaHelper.new(1250);

// Load all files in "startup" dir
(thisProcess.nowExecutingPath.dirname ++ "/startup/*").pathMatch.do({
	|item, i|
	postln("Loading " ++ item);
	item.load;
});

// This function will run when the Java app is ready and listening
OSCFunc( { |msg|

	"Creating Synths".postln;
	// [name, min, max, default]
	~java.sendDefinitions;

}, '/start/ready', recvPort:NetAddr.langPort);

"ready".postln;

)

