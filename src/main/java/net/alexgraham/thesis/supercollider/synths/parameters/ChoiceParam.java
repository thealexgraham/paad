package net.alexgraham.thesis.supercollider.synths.parameters;

import java.io.Serializable;
import java.util.Arrays;

import com.sun.org.apache.xml.internal.utils.ObjectPool;

public class ChoiceParam implements Param, Serializable {
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
	
	public Object getChoiceValue() {
		return choiceValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String name;
	public String choiceName;
	public Object[] choiceArray;
	private Object choiceValue;
	private String choiceType;
	
	public String getChoiceType() { return choiceType; }
	public void setChoiceType(String choiceType) { this.choiceType = choiceType; }
	
	public ChoiceParam(String name, String choiceName, Object[] choiceArray) {
		this.name = name;
		this.choiceName = choiceName;
		this.choiceArray = Arrays.copyOf(choiceArray, choiceArray.length);
	}
	
	public ChoiceParam(String name, String choiceName, Object choiceValue) {
		this.name = name;
		this.choiceName = choiceName;
		this.choiceValue = choiceValue;
	}

	public ChoiceParam(String name, String choiceName, Object choiceValue,
			String choiceType) {
		this.name = name;
		this.choiceName = choiceName;
		this.choiceValue = choiceValue;
		this.choiceType = choiceType;
	}

	public String getName() {
		return name;
	}
}
