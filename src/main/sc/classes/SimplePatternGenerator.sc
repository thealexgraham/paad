SimplePatternGenerator {
	var patternFunction;

	*new {
		^super.new.init();
	}

	init {

	}

	getCurrentPattern {
		^patternFunction.value;
	}

}