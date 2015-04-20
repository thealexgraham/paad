TestClass {
var testNumber;

	*new { |testNumber|
		^super.new.init(testNumber);
	}

	init { |testNumber|
		testNumber = testNumber;
	}

	getNumber {
		testNumber.postln;
	}

}