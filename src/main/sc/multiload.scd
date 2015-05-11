
(
{
	var choices = [], dir, path;
	dir = "C:/supercollider/sounds/";
	path = PathName.new(dir);

	// Go through each file in the directory
	path.folders.do({ |aFolder|
		var buffers = [];
		aFolder.filesDo({ |aFile|
			// Load the file into a buffer
			var buffer = Buffer.read(s, aFile.fullPath);
			buffers = buffers.add(buffer.bufnum);
		});

		//Store the filename and a reference to
		// the buffer in the choices array
		choices = choices.add([aFolder.folderName, buffers]);
	});
	// Return the choices array
	choices;
}.value

)