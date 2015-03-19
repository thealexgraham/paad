(
var java;
o = ServerOptions.new;
//o.device = "S/PDIF (6- M-Audio FastTrack Pr";

s = Server(\javaSCServer, NetAddr("127.0.0.1", 57320), o);
Server.default = s;
~server = s;
"my file is running".postln;

s.waitForBoot({
	"server ready".postln;

	// Send java the port sclang is listening on
	NetAddr.langPort.postln;
	javaCommand("receivePort", NetAddr.langPort);

	// Update the CPU usage
	Routine { arg inval;
		loop {
			// thisThread refers to the routine.
			/*postf("|\n");
			postf("avgCPU:%\n", s.avgCPU);
			postf("peakCPU:%\n", s.peakCPU);*/
			javaCommand("peakCPU", s.peakCPU);
			javaCommand("avgCPU", s.avgCPU);
			0.5.yield;
			nil;
		}
	}.play;

	// Create Network
	n = NetAddr("127.0.0.1", NetAddr.langPort);

	// Create a new java helper at the default port
	~java = JavaHelper.new(1280);
	// Just make 5 faders, 4 mix, one Master, have the 4 mix automatically connect to the master
	~java.addDefinition(\fader, \effect, { | out=0 inBus mute=1 amp=1 |
			Out.ar(out, In.ar(inBus, 2) * mute * amp)
		}, [[\amp, 0, 1, 0.5]]
	);


	// Load all files in "startup" dir
	(thisProcess.nowExecutingPath.dirname ++ "/startup/*").pathMatch.do({
		|item, i|
		postln("Loading " ++ item);
		item.load;
	});

	// This function will run when the Java app is ready and listening
	OSCFunc({|msg|
		"Creating Synths".postln;

		if (~reboot != true, {
			~java.sendDefinitions; // problem os jere
		});

	}, '/start/ready', recvPort:NetAddr.langPort);
	//s.record;
}, 100);

)
	