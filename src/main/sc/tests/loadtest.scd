(
{
	var choices, dir, path;
	choices = [];
	dir = thisProcess.nowExecutingPath.dirname ++ "/../sounds/";
	path = PathName.new(dir);

	// Go through each file in the directory
	path.filesDo({ |aFile|
		var buffer, fileName;
		aFile.postln;
		// Load the file into a buffer
		buffer = Buffer.read(s, aFile);
		// Get the filename
		fileName = aFile.fileName;
		//Store the filename and a reference to the buffer in the choices array
		choices = choices.add([fileName, buffer.bufnum]);
	});

	// Return the choices array
	choices;
}.value
)