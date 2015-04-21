package net.alexgraham.thesis.supercollider.models;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.defs.Def;

public class LaunchTreeModel {
	
	TreeMap<String, DefaultMutableTreeNode> categories = new TreeMap<String, DefaultMutableTreeNode>();
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	DefaultTreeModel treeModel;
	
	Def def = new Def("test", App.sc);
	public LaunchTreeModel() {
		treeModel = new DefaultTreeModel(root);
	}
	
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
	
	public DefaultMutableTreeNode getRoot() {
		return root;
	}
	
	public void addSynthDef(Def def) {
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
		sortTree();
	}
	
	public void sortTree() {
	    treeModel.reload(sort(root));
	}

	public DefaultMutableTreeNode sort(DefaultMutableTreeNode node) {

	    //sort alphabetically
	    for(int i = 0; i < node.getChildCount() - 1; i++) {
	        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	        String nt = child.getUserObject().toString();

	        for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
	            DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);
	            String np = prevNode.getUserObject().toString();

	            if(nt.compareToIgnoreCase(np) > 0) {
	                node.insert(child, j);
	                node.insert(prevNode, i);
	            }
	        }
	        if(child.getChildCount() > 0) {
	            sort(child);
	        }
	    }

	    //put folders first - normal on Windows and some flavors of Linux but not on Mac OS X.
	    for(int i = 0; i < node.getChildCount() - 1; i++) {
	        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
	        for(int j = i + 1; j <= node.getChildCount() - 1; j++) {
	            DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);

	            if(!prevNode.isLeaf() && child.isLeaf()) {
	                node.insert(child, j);
	                node.insert(prevNode, i);
	            }
	        }
	    }

	    return node;

	}
}