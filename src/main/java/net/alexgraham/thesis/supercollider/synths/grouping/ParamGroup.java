package net.alexgraham.thesis.supercollider.synths.grouping;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;


public class ParamGroup {
	private String name;
	private List<ParamModel> params = new ArrayList<ParamModel>();
	private Color groupColor;

	public ParamGroup(String name) {
		this.name = name;
		init();
	}
	
	public ParamGroup() {
		init();
	}
	
	private void init() {
		Random random = new Random();
		groupColor = new java.awt.Color(random.nextInt(255));
	}

	public void addParamModel(ParamModel model) {
		if (!params.contains(model)) {
			params.add(model);			
		}
	}
	
	public void removeParamModel(ParamModel model) {
		params.remove(model);
	}
	
	// Getters / Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getGroupColor() {
		return groupColor;
	}

	public void setGroupColor(Color groupColor) {
		this.groupColor = groupColor;
	}
}
