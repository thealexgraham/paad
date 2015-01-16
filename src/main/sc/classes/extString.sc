+ String {
	javaCommand { |other = ""|
		"|".postln;
		(this++":"++other).postln;
	}

	tildaPut { |id, item|
		^this.toLower.asSymbol.envirPut(id, item);
	}
	tildaGet{ |id, item|
		^this.toLower.asSymbol.envirGet.at(id, item);
	}


}