package net.alexgraham.thesis.supercollider.models;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.corba.se.spi.orb.StringPair;
import com.sun.org.apache.bcel.internal.generic.NEW;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;

public class ParamGroupModel {
	private List<ParamGroup> exportGroups = new CopyOnWriteArrayList<ParamGroup>();
	
    String[] colors = {"FF0000", "00FF00", "0000FF", "FFFF00", "FF00FF", "00FFFF", "000000", 
	    "800000", "008000", "000080", "808000", "800080", "008080", "808080", 
	    "C00000", "00C000", "0000C0", "C0C000", "C000C0", "00C0C0", "C0C0C0", 
	    "400000", "004000", "000040", "404000", "400040", "004040", "404040", 
	    "200000", "002000", "000020", "202000", "200020", "002020", "202020", 
	    "600000", "006000", "000060", "606000", "600060", "006060", "606060", 
	    "A00000", "00A000", "0000A0", "A0A000", "A000A0", "00A0A0", "A0A0A0", 
	    "E00000", "00E000", "0000E0", "E0E000", "E000E0", "00E0E0", "E0E0E0"};
    private int colorIndex = 0;
	
	public ParamGroupModel() {
	}
	
	public ParamGroup getExportGroupByName(String name) {
		ParamGroup nameGroup = null;
		
		for (ParamGroup group : exportGroups) {
			if (group.getName().equals(name)) 
				nameGroup = group;
		}
		
		return nameGroup;
	}
	
	public List<ParamGroup> getExportGroups() {
		return exportGroups;
	}
	
	public void setExportGroups(List<ParamGroup> exportGroups) {
		this.exportGroups = exportGroups;
	}
	
	public void newExportGroup() {
		// Get name?
		String name = "new group";
		ParamGroup group = new ParamGroup(name);
		exportGroups.add(group);
	}
	
	public ParamGroup newExportGroup(String name) {
		ParamGroup group = new ParamGroup(name);
		group.setGroupColor(Color.decode("#" + colors[colorIndex++]));
		exportGroups.add(group);
		return group;
	}
	
}
