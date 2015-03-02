package net.alexgraham.thesis.supercollider.models;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import com.sun.org.apache.xml.internal.security.Init;

import net.alexgraham.thesis.supercollider.SaveHelper;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class DataModel {
	
	private LaunchTreeModel launchTreeModel;
	private DefModel defModel;
	private SynthModel synthModel;
	private PlayerModel playerModel;
	private ConnectionModel connectionModel;
	
	private DataStore dataStore;

	private LineConnectPanel lineConnectPanel;

	public void setLineConnectPanel(LineConnectPanel lineConnectPanel) {
		this.lineConnectPanel = lineConnectPanel;
	}
	
	public DataModel() throws SocketException {
		defModel = new DefModel();
		synthModel = new SynthModel();
		playerModel = new PlayerModel();
		launchTreeModel = new LaunchTreeModel();
		connectionModel = new ConnectionModel();
		
		dataStore = new DataStore();
		init();
	}
	
	public void init() {
		synthModel.setSynthListModel(dataStore.getSynthListModel());
		connectionModel.setConnections(dataStore.getConnections());
		defModel.setDefListModel(dataStore.getDefListModel());
		
	}
	
	
	public void addModule(ModulePanel module) {
		dataStore.addModule(module);
	}
	
	public void removeModule(ModulePanel module) {
		dataStore.removeModule(module);
		
	}
	
	public ArrayList<ModulePanel> getModulePanels() {
		ArrayList<ModulePanel> list = new ArrayList<ModulePanel>();
		for (Enumeration<ModulePanel> e = Collections.enumeration(dataStore.getModules()); e.hasMoreElements();)  {
			ModulePanel module = e.nextElement();
			list.add(module);
		}
		return list;
	}
	
	
	
	public void saveInstances() throws IOException {
    	// Write to disk with FileOutputStream
		String filename = "test";
		SaveHelper save = new SaveHelper();
		save.writeObject(filename, dataStore);
//		save.writeObject(filename, connectionModel);
		
		for (Synth synth : synthModel.getSynths()) {
			System.out.println(synth.toString());
		}
	}
	
	public void loadInstances() throws ClassNotFoundException, IOException {
		String filename = "test";
		SaveHelper save = new SaveHelper();
		dataStore = (DataStore) save.readObject(filename, dataStore);
		
		init();
		
		for (Synth synth : synthModel.getSynths()) {
			System.out.println(synth.toString());
		}
		
		// Start and readd to lineconnect
		synthModel.refreshInstances();
		lineConnectPanel.refreshModules();
		for (Connection connection : dataStore.getConnections()){
			connection.connectModules();
		}
	}

	public LaunchTreeModel getLaunchTreeModel() {
		return launchTreeModel;
	}


	public void setLaunchTreeModel(LaunchTreeModel launchTreeModel) {
		this.launchTreeModel = launchTreeModel;
	}


	public DefModel getDefModel() {
		return defModel;
	}


	public void setDefModel(DefModel defModel) {
		this.defModel = defModel;
	}


	public SynthModel getSynthModel() {
		return synthModel;
	}


	public void setSynthModel(SynthModel synthModel) {
		this.synthModel = synthModel;
	}


	public PlayerModel getPlayerModel() {
		return playerModel;
	}


	public void setPlayerModel(PlayerModel playerModel) {
		this.playerModel = playerModel;
	}


	public ConnectionModel getConnectionModel() {
		return connectionModel;
	}


	public void setConnectionModel(ConnectionModel connectionModel) {
		this.connectionModel = connectionModel;
	}

}
