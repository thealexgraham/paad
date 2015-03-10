(
var java;
o = ServerOptions.new;

// s = Server(\fmodSCServer, NetAddr("127.0.0.1", 57320), o);
Server.default = s;
~server = s;

s.waitForBoot({
	// Create a new java helper at the default port
	~java = JavaHelper.new(1280);

	~java.java = false; // Loading in FMOD
	~java.ready = true;

	// Load all files in "startup" dir

	(thisProcess.nowExecutingPath.dirname ++ "/alldefinitions.scd").load

	~java.readyAction = {
		(thisProcess.nowExecutingPath.dirname ++ "/start.scd").load;
	};
}, 100);

)
	