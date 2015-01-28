PatternChooser {
	var patterns;
	var weights;
	var <>currentPattern;

	*new {
		^super.new.init();
	}

	init {
		patterns = List.new(1);
		weights = List.new(1);
	}

	getCurrentPattern {
		^currentPattern;
	}
	choosePattern {
		currentPattern = patterns.wchoose(weights.asArray.normalizeSum);
		^currentPattern;
	}

	addPattern { |pattern, weight|
		patterns.add(pattern);
		weights.add(weight);
	}

	removePatternAt { |index|
		patterns.removeAt(index);
		weights.removeAt(index);
	}

	changeWeightAt { |index, value|
		weights[index] = value;
	}
}