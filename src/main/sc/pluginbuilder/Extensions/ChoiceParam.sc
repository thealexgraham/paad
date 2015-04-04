ChoiceParam {
	var <>name;
	var <>choiceName;
	var <>value;
	var <>ownerId;

	*new { arg name, choiceName, value;
		^super.new.init(name, choiceName, value);
	}

	init { |newName, newChoiceName, newValue|
		name = newName;
		value = newValue;
	}

	set { |newChoiceName, newValue|
		var net;
		if (value != newValue, {
			net = NetAddr("127.0.0.1", ~java.sendPort);
			value = newValue;
			net.sendMsg("/"++ownerId++"/"++name++"/change", value);
			choiceName = newValue;
		});

		^value;
	}

	setSilent { |newValue|
		value = newValue;
		^value;
	}

}
