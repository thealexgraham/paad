package net.alexgraham.thesis.supercollider.models;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class DataStore implements Serializable {

	private DefaultListModel<Instance> synthListModel = new DefaultListModel<Instance>();
	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();	
	private Hashtable<String, Def> defTable = new Hashtable<String, Def>();
	private List<ParamGroup> exportGroups = new CopyOnWriteArrayList<ParamGroup>();
	
//	private CopyOnWriteArrayList<ModulePanel> modules = new CopyOnWriteArrayList<ModulePanel>();
	
	public Hashtable<String, Def> getDefTable() {
		return defTable;
	}

	public DefaultListModel<Instance> getSynthListModel() {
		return synthListModel;
	}
	public CopyOnWriteArrayList<Connection> getConnections() {
		return connections;
	}
	
	public List<ParamGroup> getExportGroups() {
		return exportGroups;
	}
	
	
//	public CopyOnWriteArrayList<ModulePanel> getModules() {
//		return modules;
//	}
//	public void addModule(ModulePanel module) {
//		modules.add(module);
//	}
//	public void removeModule(ModulePanel module) {
//		modules.remove(module);		
//	}
	
	
}
