package net.alexgraham.thesis.supercollider.models;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.SynthDef;

public class LaunchTreeModel {
	HashMap<String, DefaultMutableTreeNode> categories = new HashMap<String, DefaultMutableTreeNode>();
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	DefaultTreeModel treeModel;
	
	SynthDef def = new SynthDef("test", App.sc);
	public LaunchTreeModel() {
		treeModel = new DefaultTreeModel(root);
	}
	
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	
	public DefaultMutableTreeNode getRoot() {
		return root;
	}
	
	public void addSynthDef(SynthDef def) {
		String className = def.getClass().getSimpleName();
		if (categories.containsKey(className)) {
			DefaultMutableTreeNode category = categories.get(className);
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(def);
			category.add(newChild);
		} else {
			DefaultMutableTreeNode newCategory = new DefaultMutableTreeNode(className);
			root.add(newCategory);
			categories.put(className, newCategory);
			newCategory.add(new DefaultMutableTreeNode(def));
		}
	}
}