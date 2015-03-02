package net.alexgraham.thesis.supercollider.models;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import net.alexgraham.thesis.supercollider.SaveHelper;
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
//		dataStore.addModule(module);
	}
	
	public void removeModule(ModulePanel module) {
//		dataStore.removeModule(module);
		
	}
	
	public ArrayList<ModulePanel> getModulePanels() {
		ArrayList<ModulePanel> list = new ArrayList<ModulePanel>();
//		for (Enumeration<ModulePanel> e = Collections.enumeration(dataStore.getModules()); e.hasMoreElements();)  {
//			ModulePanel module = e.nextElement();
//			list.add(module);
//		}
		return list;
	}
	
	public void clearData() {
		// Close all instances first
		lineConnectPanel.refreshModules(); // Deletes everything
		synthModel = new SynthModel();
		synthModel.closeInstances();
	}
	
	public void saveData(File outFile){
    	// Write to disk with FileOutputStream
		SaveHelper.writeObject(outFile, dataStore);

	}
	
	public void loadData(File inFile) {
		
		// Close all instances first
		synthModel.closeInstances();
		
		Object o = SaveHelper.readObject(inFile);
		
		if (o instanceof DataStore) {
			dataStore = (DataStore) o;
		} else {
			System.err.println("Error loading DataStore (wrong type)");
			return;
		}
		lineConnectPanel.refreshModules(); // Deletes everything
		
		// Have models point to new datastore (necessary?)
		init();
		
		// Start and read to lineconnect
		synthModel.refreshInstances();

		
		// Reconnect connections in SCLang
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
