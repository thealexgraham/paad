+ JavaHelper {
	createParamsList { arg ...args;
		var params = [];
		args.do({ |item, i|
			if (item[0] == \range,
				{ params = params.addAll([item[1], item[2]]); },
				{ params = params.add(item)}
			);
		});
		^params;
	}
	addFloat { | name, min, max, default |
		^[name, \float, min, max, default];
	}
	addChoice { |name, defaultName, defaultValue, choiceType|
		^[name, \choice, [defaultName, defaultValue], choiceType.preserveKeywords];
	}
	addInt { |name, min, max, default |
		^[name, \int, min, max, default];
	}
	addRange{ |name, type, min, max, lower, upper|
		^[\range, [(name++"Min").asSymbol, type, min, max, lower],[(name++"Max").asSymbol, type, min, max, upper]];
	}

	setReturnType { |type|
		^[\return, \return, type.preserveKeywords];
	}
}

+ Collection {
	preserveKeywords {
		^this.deepCollect(15, { |item|
			if (item.isSymbolWS == true,
				{ item = ("\\" ++ item.asString); }
			);
			item;
		});
	}
}

+ Symbol {
	preserveKeywords {
		^("\\" ++ this.asString);
	}
}