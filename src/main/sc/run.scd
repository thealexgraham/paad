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
		Buffer.alloc(s, 0); // Empty buffer

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
	~java.addDefinition(\fader, \effect, { | outBus=0 inBus mute=1 amp=1 |
			Out.ar(outBus, In.ar(inBus, 2) * mute * amp)
		}, [[\amp, 0, 1, 0.5]]
	);

	~java.addDefinition("LoadAction", \specialAction);
	~java.addDefinition("PlayAction", \specialAction);
	~java.addDefinition("StopAction", \specialAction);

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

		~loadAction.sendActionMessage;

	}, '/start/ready', recvPort:NetAddr.langPort);

	OSCFunc({|msg|
		~playing = false;
		~playRoutine = Task {
			0.05.wait;
			~playing = false;
			~masterFader.set(\mute, 0);

			// Send the special stop action message
			~stopAction.sendActionMessage;
			"needed to stop".postln;
		};
		~playRoutine.start;

		// Wait until Java is ready to receive the rest
		OSCdef(\process, { |msg, time, addr|
			// "got message".postln;
			if(~playing == false, {
				~playing = true;
				~masterFader.set(\mute, 1);

				// Send the special play action message
				~playAction.sendActionMessage;
			});


			// Restart the play routine
			~playRoutine.stop;
			~playRoutine.reset;
			~playRoutine.start;

		}, '/process');
	}, '/live/start');

	OSCFunc({|msg|
		~playRoutine.stop;
		~masterFader.set(\mute, 1);
	}, '/live/stop');

	//s.record;
}, 100);

)
	