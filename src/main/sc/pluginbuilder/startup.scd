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
	~java.java = false;

	// Load all files in "startup" dir

	(thisProcess.nowExecutingPath.dirname ++ "/alldefinitions.scd").load;
	~java.readyAction_({
		(thisProcess.nowExecutingPath.dirname ++ "/start.scd").load;
		~loadAction.sendActionMessage;
	});

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


}, 100);

)
	