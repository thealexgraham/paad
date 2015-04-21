SpecialAction {
	var instanceId;

	*new { |name|
		^super.new.init(name);
	}

	init { |name|
		listeners = Set.new;
		instanceId = name;
	}

	addListener { |obj, action|
		listeners.add((object: obj, action:action));
	}

	removeListener { |obj, action|
		listeners.remove((object:obj, action:action));
	}

	sendActionMessage {
		var net = NetAddr("127.0.0.1", ~java.sendPort);
		listeners.do({ |item, i|
			item.at(\object).doAction(item.at(\action));
		});
		// JAVA ONLY
		net.sendMsg("/"++instanceId++"/action/sent", 1);
	}
}