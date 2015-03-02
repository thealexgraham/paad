package net.alexgraham.thesis.supercollider.models;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class DataStore implements Serializable {

	private DefaultListModel<Instance> synthListModel = new DefaultListModel<Instance>();
	private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();
	private CopyOnWriteArrayList<ModulePanel> modules = new CopyOnWriteArrayList<ModulePanel>();
	private DefaultListModel<Def> defListModel = new DefaultListModel<Def>();

	public DefaultListModel<Def> getDefListModel() {
		return defListModel;
	}
	public DefaultListModel<Instance> getSynthListModel() {
		return synthListModel;
	}
	public CopyOnWriteArrayList<Connection> getConnections() {
		return connections;
	}
	public CopyOnWriteArrayList<ModulePanel> getModules() {
		return modules;
	}
	public void addModule(ModulePanel module) {
		modules.add(module);
	}
	public void removeModule(ModulePanel module) {
		modules.remove(module);		
	}
	
	
}
