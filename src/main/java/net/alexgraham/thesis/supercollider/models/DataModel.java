package net.alexgraham.thesis.supercollider.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.ArrayList;

import jdk.Exported;
import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.FileHelper;
import net.alexgraham.thesis.supercollider.SaveHelper;
import net.alexgraham.thesis.supercollider.sync.StepSyncer;
import net.alexgraham.thesis.supercollider.sync.SyncAction;
import net.alexgraham.thesis.supercollider.sync.Syncer;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class DataModel {
	
	private LaunchTreeModel launchTreeModel;
	private DefModel defModel;
	private SynthModel synthModel;
	private PlayerModel playerModel;
	private ConnectionModel connectionModel;
	private ParamGroupModel paramGroupModel;
	
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
		paramGroupModel = new ParamGroupModel();
		
		dataStore = new DataStore();
		init();
	}
	
	public void init() {
		synthModel.setSynthListModel(dataStore.getSynthListModel());
		connectionModel.setConnections(dataStore.getConnections());
		defModel.setDefTable(dataStore.getDefTable());
		
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
		StepSyncer refreshSyncer = new StepSyncer();
		
		refreshSyncer.addStep(synthModel.refreshInstances()); // Refresh the instances (Returns Syncer)
		
		// Reconnect Modules
		refreshSyncer.addStep(new SyncAction() {
			@Override
			public void doAction() {
				// Reconnect connections in SCLang
				for (Connection connection : dataStore.getConnections()){
					connection.connectModules();
				}
			}
		});
		
		refreshSyncer.run();
	}
	
	
	public void createExportRunFile() {
		// Create a file with all defs for each instance written explicitly
		
		File fout = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		try {
			fout = new File(FileHelper.getSCCodeDir() + "/export/alldefinitions.scd");
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (Instance instance : synthModel.getInstances()) {
				// Write definition
				instance.getDef().writeDefintion(bw);
				bw.newLine();
			}
			bw.close();
			
			// Start Messages
			fout = new File(FileHelper.getSCCodeDir() + "/export/start.scd");
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
 
			bw.write("(");
			bw.newLine();
			
			bw.write("var net = NetAddr.new(\"127.0.0.1\", NetAddr.langPort);");
			bw.newLine();
			
			// Temporarily write messages to file instead of send OSC message
			App.sc.setExportWriter(bw);
			for (Instance instance : synthModel.getInstances()) {
				instance.start();
			}
			
			// Write sync messages???
			
			for (Connection connection : dataStore.getConnections()){
				connection.connectModules();
			}
			
			// Go back to sending OSC Messages
			App.sc.stopExportWriting();
			
			bw.write(")");
			bw.close();
			
			FileHelper.openIde(fout);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create file with server run
		
		// Load all defs here
		
		
		
		// add/start all instances in SC
		// Connect all instances in SC
		
		// Create FMOD plugin with specified paramc stuff
		
		// Copy necessary files into a folder
	}
//	
//	public void createPlugins() {
//		String replaceFileString = "";
//		replaceFileString.
//	}

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

	public ParamGroupModel getParamGroupModel() {
		return paramGroupModel;
	}

}
