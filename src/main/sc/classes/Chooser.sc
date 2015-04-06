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
		^choices[choiceIndex];
	}

	updateListeners {
		listeners.do({ |item, i|
			"Updating listener".postln;
			this.updateParamChoice(item);
		});
	}

	updateParamChoice { | item |
		var currentChoice = this.getCurrentChoice;
		item.set(currentChoice[0], currentChoice[1]); // name, value
	}

}