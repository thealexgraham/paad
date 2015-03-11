// Alex Graham
// A class that generates FMOD plugins to control SuperCollider synths

JPluginBuilder {
	*generateCode { |pluginName, params, builddir = false |
		var out, in, template, replace, blocks;
		var batch;
		var iterates, singles, chars;
		var first = "";

		if( builddir == false,
			{ builddir = thisProcess.nowExecutingPath.dirname; }
		);

		blocks = List.new;

		// Open replacement file
		in = File(builddir ++ "/templates/replace.cpp", "r");
		replace = in.readAllString;
		in.close();

		// Get all code blocks to replace
		chars = "!\"$%&'()*+,./:;<=>?@\^_`{|}~-";
		iterates = replace.findRegexp("### ITERATE_START ###[A-Za-z0-9_ \t\r\n\v\f" ++ chars ++ "]+### ITERATE_END ###");
		singles = replace.findRegexp("### SINGLE_START ###[A-Za-z0-9_ \t\r\n\v\f" ++ chars ++ "]+### SINGLE_END ###");

		iterates.do( { |item, i|
			var str, code, position, final;
			// Remove the code block indicators
			code = item[1].replace("### ITERATE_START ###", "")
			.replace("### ITERATE_END ###", "");

			// Get the position indicator (and remove it from the code)
			position = code.findRegexp("%%%[A-Z0-9_]+%%%");
			position = position[0][1];
			code = code.replace(position,"");

			final = "///" ++ position;
			// Go through each parameters and add the code block with its info to the final code
			~params.do({ |item i|
				var instanceName = item[0].asString, instanceType = item[1].asString, instanceId = item[2].asString, param = item[3].asString;
				var min = item[4], max = item[5], default = item[6];
				final = final ++ code.replace("^^param_name^^", param.toUpper)
				.replace("^^param_name^", this.firstToUpper(param))
				.replace("^param_name^", param)
				.replace("^instance_name^", instanceName)
				.replace("^^instance_name^^", instanceName.toUpper)
				.replace("^^instance_name^", this.firstToUpper(instanceName))
				.replace("^instance_type^", instanceType)
				.replace("^instance_id^", instanceId)
				.replace("^min^", min)
				.replace("^max^", max)
				.replace("^default^", default);
			});
			// Add this block and its position to replace later
			blocks.add([position, final]);
		});

		// Same as the iterators, but these blocks only happen once (current assume they are just for the synth name)
		singles.do({|item, i|
			var str, code, position, final;
			code = item[1].replace("### SINGLE_START ###", "")
			.replace("### SINGLE_END ###", "");
			position = code.findRegexp("%%%[A-Z0-9_]+%%%");
			position = position[0][1];
			code = code.replace(position,"");

			final = "///" ++ position;
			final = final ++ code.replace("^^synth_name^^", pluginName.toUpper)
			.replace("^^synth_name^", this.firstToUpper(pluginName))
			.replace("^synth_name^", pluginName)
			.replace("define", "#define"); // Because Im not very good at regex

			blocks.add([position, final]);
		});

		// Template is where all the code will be replaced into
		in = File(builddir ++ "/templates/template.cpp", "r");
		template = in.readAllString;
		in.close();

		// The file to write
		out = File(builddir ++ "/SCBuilder/fmod-osc/src/" ++ "sc-plugin" ++ ".cpp", "w");

		// Replace all the blocks with their position
		blocks.do({|item, i|
			template = template.replace(item[0], item[1]);
		});

		// Write and close the file
		out.write(template);
		out.close();

		// Create a batch file for building the dll
		in = File(builddir ++ "/templates/build-template.bat", "r");
		batch = in.readAllString;
		batch = batch.replace("^synth_name^", pluginName);
		in.close();

		out = File(builddir ++ "/build-plugin.bat", "w");
		out.write(batch);
		out.close();
		^"Plugin creation sucessful, please run batch file!".postln;
	}

	*createListeners { |synthName, params|
		var paramsSynthFormat;

		//n = NetAddr("127.0.0.1", 57120);

		// Get just the parameters and the default values
		paramsSynthFormat = Array.fill(params.size, { |i|
			[params[i][0], params[i][3]];
		});

		// Create a dictionary to store the running synths (for multiple copies of plugin)
		synthName.toLower.asSymbol.envirPut(Dictionary.new);

		// Whenever plugin is created (or reset), this will create a Synth and add it to the dictionary
		OSCresponder(nil, "/"++synthName++"/start", { arg time, resp, msg;
			var id = msg[1];
			// Create synth defs at this location
			synthName.toLower.asSymbol.envirGet.put(id, Synth.new(synthName, paramsSynthFormat));
			("Plugin connected, adding synths at" + id).postln;
		}).add;

		// Whenever the plugin is removed (or killed internally) this will free the synth
		OSCresponder(nil, "/"++synthName++"/dying", { arg time, resp, msg;
			// Free synth defs at this id
			var id = msg[1];
			synthName.toLower.asSymbol.envirGet.at(id).free;
			("Plugin disconnected, freeing synths at" + id).postln;
		}).add;

		// Set up a listener for each parameter for this synth
		params.do({|item, i|
			OSCresponder(nil,"/"++synthName++"/"++item[0].asString, { arg time, resp, msg;
				// Set float1
				var id = msg[1], val = msg[2];
				synthName.toLower.asSymbol.envirGet.at(id).set(item[0], val);
				("Changing" + synthName + id + item[0] + val).postln;
			}).add;
			//("/"++synthName++"/"++item[0].asString).postln;
		});
		^("OSC Responders ready for" + synthName);
	}

	*firstToUpper { |aString|
		^aString[0].toUpper.asString ++ aString[1..];
	}
}