ChoiceParamBus {
	var <>name;
	var <>choiceName;
	var <>value;
	var <>ownerId;
	var <>bus;

	var defaultName;
	var defaultValue;

	var choiceObj;

	*new { arg name, choiceName, value;
		^super.new.init(name, choiceName, value);
	}

	init { |newName, newChoiceName, newValue|
		name = newName;
		value = newValue;
		choiceName = newChoiceName;

		bus = Bus.control.set(value);

		defaultName = newChoiceName;
		defaultValue = newValue;
	}

	set { |newChoiceName, newValue|
		var net;
		if (value != newValue, {
			ownerId.postln;
			net = NetAddr("127.0.0.1", ~java.sendPort);
			value = newValue;
			bus.set(value);
			net.sendMsg("/"++ownerId++"/"++name++"/change", newChoiceName, newValue.asString);
			choiceName = newChoiceName;
		});

		^value;
	}

	setChoiceName { |newChoiceName|
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		net.sendMsg("/"++ownerId++"/"++name++"/change", newChoiceName);
		choiceName = newChoiceName;
	}

	setSilent { |newValue|
		value = newValue;
		^value;
	}

	setChoiceObj { |obj|
		choiceObj = obj;
	}

	removeChoiceObj { |obj|
		choiceObj = nil;
		// this.setChoiceName(defaultName);
		this.set(defaultName, defaultValue);
	}
}
