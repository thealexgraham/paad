+ Array {
	// Adds messages in toAdd to the beginning of this array
	insertAll { |pos, toAdd|
		var newArray = Array.newFrom(this);
		toAdd.reverseDo({| item, i|
			newArray = newArray.insert(pos, item);
		});
		^newArray;
	}

	last {
		^this.at(this.size - 1);
	}
}