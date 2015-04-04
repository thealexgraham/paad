(
var java;
o = ServerOptions.new;

s = Server(\fmodSCServer, NetAddr("127.0.0.1", 57421), o);
Server.default = s;
~server = s;

s.waitForBoot({
	// Create a new java helper at the default port
	~java = JavaHelper.new(1280);

	~java.ready = true;

	// Load all files in "startup" dir

	(thisProcess.nowExecutingPath.dirname ++ "/alldefinitions.scd").load;
	~java.readyAction_({
		(thisProcess.nowExecutingPath.dirname ++ "/start.scd").load;
	});

	~playing = false;
	~playRoutine = Task {
		0.05.wait;
		~playing = false;
		~masterFader.set(\mute, 0);
		"needed to stop".postln;
	};
	~playRoutine.start;

			// Wait until Java is ready to receive the rest
	OSCdef(\process, { |msg, time, addr|
		// "got message".postln;
		~playing = true;
		~masterFader.set(\mute, 1);

		// Restart the play routine
		~playRoutine.stop;
		~playRoutine.reset;
		~playRoutine.start;

	}, '/process');


}, 100);

)
	