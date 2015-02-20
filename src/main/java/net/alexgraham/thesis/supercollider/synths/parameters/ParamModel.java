package net.alexgraham.thesis.supercollider.synths.parameters;

import net.alexgraham.thesis.supercollider.synths.Instance;

public interface ParamModel {
	public Instance getOwner();
	public void setOwner(Instance owner);

	public String getName();
	public void setName(String name);
}
