ChoiceParam {
	var <>name;
	var <>choiceName;
	var <>value;
	var <>ownerId;

	var defaultName;
	var defaultValue;

	*new { arg name, choiceName, value;
		^super.new.init(name, choiceName, value);
	}

	init { |newName, newChoiceName, newValue|
		name = newName;
		value = newValue;
		choiceName = newChoiceName;

		defaultName = newName;
		defaultValue = newValue;
	}

	set { |newChoiceName, newValue|
		var net;
		if (value != newValue, {
			ownerId.postln;
			net = NetAddr("127.0.0.1", ~java.sendPort);
			value = newValue;
			net.sendMsg("/"++ownerId++"/"++name++"/change", newChoiceName);
			choiceName = newChoiceName;
		});

		^value;
	}

	setSilent { |newValue|
		value = newValue;
		^value;
	}

}
