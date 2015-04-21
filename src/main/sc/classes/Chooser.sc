Chooser {
	var choiceIndex;
	var choices;
	var listeners; // Should be ChoiceParams

	*new { |newChoices|
		^super.new.init(newChoices);
	}

	init { |newChoices|
		choices = newChoices;
		choiceIndex = 0;

		listeners = IdentitySet.new;
	}
	// Should have one int ParamBus that holds the choices

	addListener { |obj|
		"Adding listener".postln;
		listeners.add(obj);
		// Update the param choice now
		this.updateParamChoice(obj);
	}

	removeListener { |obj|
		listeners.remove(obj);
	}

	choose { |index|
		choiceIndex = index;
		this.updateListeners; // tell all our friends
	}

	getCurrentChoice {
		choices.postln;
		choiceIndex.postln;
		^choices[choiceIndex];
	}

	getCurrentValue {
		^this.getCurrentChoice[1];
	}

	updateListeners {
		listeners.do({ |item, i|
			this.updateParamChoice(item);
		});
	}

	updateParamChoice { | item |
		var currentChoice = this.getCurrentChoice;
		if (item.class == ChoiceParamBus,
			{ item.set(currentChoice[0], currentChoice[1]); }, // name, value
			{ item.setChoiceName(currentChoice[0]); } // just set the name
		);
		//item.set(currentChoice[0], currentChoice[1]); // name, value
	}

}