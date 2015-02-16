+ String {
	javaCommand { |other = ""|
		"|".postln;
		(this++":"++other).postln;
	}

	postSilent {
		("<-"++this).postln;
	}

	tildaPut { |item|
		^this.toLower.asSymbol.envirPut(item);
	}

	tildaGet {
		^this.toLower.asSymbol.envirGet;
	}


	nameGet {
		if (this.tildaGet == nil, {
			// Create dictionary if not created yet
			this.tildaPut(Dictionary.new);
		});

		^this.tildaGet;
	}

	namePut { |item|
		^this.tildaPut(item);
	}

	// Gets whatever is at the ID (defining the type)
	idGet { |id|
		if (this.nameGet.at(id) == nil, {
			// Create dictionary if not created yet
			this.tildaPut(Dictionary.new);
		});

		^this.nameGet.at(id);
	}
	
	idPut { |id, value|
		^this.nameGet.put(id, value);
	}

	idRemove { |id|
		^this.tildaGet.removeAt(id);
	}


}

+ Symbol {
	tildaPut { |item|
		^this.asString.toLower.asSymbol.envirPut(item);
	}

	tildaGet { |item|
		^this.asString.toLower.asSymbol.envirGet;
	}

	nameGet {
		if (this.tildaGet == nil, {
			// Create dictionary if not created yet
			this.tildaPut(Dictionary.new);
		});

		^this.tildaGet;
	}

	namePut { |item|
		^this.tildaPut(item);
	}

	// Gets whatever is at the ID (defining the type)
	idPut { |id, value|
		^this.nameGet.put(id, value);
	}

	// Gets whatever is at the ID (defining the type)
	idGet { |id|
		if (this.nameGet.at(id) == nil, {
			// Create dictionary if not created yet
			this.tildaPut(Dictionary.new);
		});

		^this.nameGet.at(id);
	}

	idRemove { |id|
		^this.tildaGet.removeAt(id);
	}

}
