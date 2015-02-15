+ String {
	javaCommand { |other = ""|
		"|".postln;
		(this++":"++other).postln;
	}

	tildaPut { |item|
		^this.toLower.asSymbol.envirPut(item);
	}
	tildaGet{ |item|
		^this.toLower.asSymbol.envirGet(item);
	}


}

+ Symbol {
	tildaPut { |item|
		^this.asString.toLower.asSymbol.envirPut(item);
	}

	tildaGet { |item|
		^this.asString.toLower.asSymbol.envirGet;
	}
}