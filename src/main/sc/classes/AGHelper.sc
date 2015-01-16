AGHelper {
	*loadFiles { |prepend, scds|
		var thisPath = thisProcess.nowExecutingPath.dirname;
		//var prepend = "spacelab";
		//var scds = ["1-synths", "2-control", "3-patterns"];

		scds.do({ |name, i|
			("Loading " ++ thisPath ++ "/" ++ prepend ++ "-" ++ name ++ ".scd").postln;
			(thisPath ++ "/" ++ prepend ++ "-" ++ name ++ ".scd").load.postln;
		});
	}

	*loadFile { |path, scd|
		("Loading " ++ path ++ "/" ++ scd).postln;
		(path ++ "/" ++ scd).load.postln;
	}

	*makeKnobs { |syn, args|

		var w, f;
		//Array of knob settings [arg keyword, min, max, default]

		//Set up window and decorator
		w= Window("decoration", Rect(500,100,800,500));
		w.view.decorator= FlowLayout(w.view.bounds, 10@10, 20@20);

		//Initialize the knobs (note EZSlider will also work, however the size should be set differently)
		f= Array.fill(args.size, { |i|
			EZKnob(w.view, 150@150, args[i][0], ControlSpec(args[i][1], args[i][2]), knobSize:Point(90,90))});

		//Set the actions and default values
		f.do({ |item, i|

			//If the array contains default values, set them
			if(args[0].size == 4) {
				syn.set(args[i][0], args[i][3]);
				item.value = args[i][3];
			};

			//Set the action
			item.action_({
				syn.set(args[i][0], item.value);
			});
		});

		w.onClose={syn.free;};
		w.front; //make GUI appear
	}

	*testPath { |test, path = false|
		if( path == false,
			{ path = thisProcess.nowExecutingPath.dirname; }
		);

		path.postln;
	}
}