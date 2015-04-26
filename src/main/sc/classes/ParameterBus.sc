ParameterBus {
	var <>name;
	var <>value;
	var <>min;
	var <>max;
	var <>bus;
	var <>ownerId;
	var <>default;

	*new { arg name, value, min, max;
		^super.new.init(name, value, min, max);
	}

	init { |newName, newValue, newMin, newMax|
		name = newName;
		value = newValue;
		min = newMin;
		max = newMax;
		default = newValue;
		bus = Bus.control.set(value);
	}

	set { |newValue|
		var net;
		if (value != newValue, {
			net = NetAddr("127.0.0.1", ~java.sendPort);
			value = newValue;
			bus.set(newValue);
			net.sendMsg("/"++ownerId++"/"++name++"/change", value);
			"sending message".postln;
		});

		^value;
	}

	setSilent { |newValue|
		value = newValue;
		bus.set(newValue);
		^value;
	}

}