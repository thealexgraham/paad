package net.alexgraham.thesis.supercollider.synths.parameters;

import java.util.Arrays;

import com.sun.org.apache.xml.internal.utils.ObjectPool;

public class ChoiceParam implements Param {
	public String getChoiceName() {
		return choiceName;
	}

	public void setChoiceName(String choiceName) {
		this.choiceName = choiceName;
	}

	public Object[] getChoiceArray() {
		return choiceArray;
	}

	public void setChoiceArray(Object[] choiceArray) {
		this.choiceArray = choiceArray;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name;
	public String choiceName;
	public Object[] choiceArray;
	
	
	public ChoiceParam(String name, String choiceName, Object[] choiceArray) {
		this.name = name;
		this.choiceName = choiceName;
		this.choiceArray = Arrays.copyOf(choiceArray, choiceArray.length);
	}
	
	public String getName() {
		return name;
	}
}
