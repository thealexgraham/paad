package net.alexgraham.thesis.supercollider.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.ArrayList;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.FileHelper;
import net.alexgraham.thesis.supercollider.SaveHelper;
import net.alexgraham.thesis.supercollider.sync.StepSyncer;
import net.alexgraham.thesis.supercollider.sync.SyncAction;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.supercollider.players.PatternPlayer;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParam;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

import org.apache.commons.io.FileUtils;

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
		paramGroupModel.setExportGroups(dataStore.getExportGroups());
		
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
		System.out.println("Loading");
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
				System.out.println("Connecting modules");
				// Reconnect connections in SCLang
				for (Connection connection : dataStore.getConnections()){
					connection.refreshConnectors();
					connection.connectModules();
				}
			}
		});
		
		refreshSyncer.run();
	}
	

	
	public boolean createExportRunFile(String patchName) {
		
		int langPort = 57125; // Port where SCLang will start on (Should give option to set)
		
		// Create a file with all defs for each instance written explicitly
		String pluginName = patchName;
		File fmodDirectory = new File("C:/Program Files (x86)/FMOD SoundSystem/FMOD Studio 1.04.04");
		
		// Directory where plugin will be exported
		File exportDirectory = new File(fmodDirectory.getAbsolutePath() + "/supercollider/fmod/" + pluginName); 
		
		// Directory where plugin build tools are found
		File pluginBuilderDirectory = new File(FileHelper.getSCCodeDir() + "/pluginbuilder");
		
		// Make the export directory if it isn't found
		exportDirectory.mkdirs();
				
		File fout = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
		try {
			// Copy the startup file to the plugin directory
			File pluginFile = new File(pluginBuilderDirectory.getAbsolutePath() + "/startup.scd");
			FileUtils.copyFile(pluginFile, new File(exportDirectory.getAbsolutePath() + "/startup.scd"));
			
			fout = new File(exportDirectory.getAbsolutePath() + "/alldefinitions.scd");
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			// Write all definitions of running instances
			for (Instance instance : synthModel.getInstances()) {
				// Write definition
				instance.getDef().writeDefintion(bw);
				bw.newLine();
			}
			bw.close();
			fos.close();
			
			// Start Messages
			fout = new File(exportDirectory.getAbsolutePath() + "/start.scd");
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
			// Connect Modules
			for (Connection connection : dataStore.getConnections()){
				connection.connectModules();
			}
			
			// Start playing Routine Players and Task runners (Maybe don't?)
			for (Instance instance : synthModel.getInstances()) {
				if (instance.getClass() == PatternPlayer.class) {
					((PatternPlayer) instance).sendPlay();
				}
				
				if (instance.getClass() == TaskRunner.class) {
					((TaskRunner) instance).sendPlay();
				}
			}
			
			// Go back to sending OSC Messages
			App.sc.stopExportWriting();
			
			bw.write(")");
			bw.close();
			
			FileHelper.openIde(fout);
			
			fos.close();
			
			
			// Create sclang_conf.yaml file
			
			fout = new File(exportDirectory.getAbsolutePath() + "/sclang_conf.yaml");
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			bw.write("includePaths:");
			bw.newLine();
			bw.write(String.format("\t[\"Extensions\", \"fmod/%s\"]", pluginName));
			bw.newLine();
			bw.write("excludePathsd:\n\t[]\npostInlineWarnings: false");
			bw.close();
			fos.close();
			
			// Create extPlatform.sc file
			
			fout = new File(exportDirectory.getAbsolutePath() + "/extPlatform.sc");
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			bw.write("+ Platform {\n\t startupFiles {\n");
			bw.write(String.format("\t\t^[\"fmod/%s/startup.scd\"];", pluginName));
			bw.write("\n\t}\n}");
			bw.close();
			fos.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		// Create FMOD plugin with specified paramc stuff
		
		for (ParamGroup exportGroup : App.paramGroupModel.getExportGroups()) {

			try {
				
				// Create temp plugin builder file
				File buildSCCode = File.createTempFile("buildplugin", ".scd");
				fos = new FileOutputStream(buildSCCode);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
				
				writePluginBuilderFile(bw, pluginBuilderDirectory, pluginName, langPort, exportGroup);
				
				bw.close();
							
				FileHelper.openIde(buildSCCode);
				
				// Read it into supercollider
				App.sc.sendCommand("\"" + buildSCCode.getAbsolutePath().replace("\\", "/") + "\"" + ".load.postln");
				// Wait to finish?
				
				Thread.sleep(1000);
				
				// Run the batch file, wait to finish
				ProcessBuilder pb = new ProcessBuilder(pluginBuilderDirectory.getAbsolutePath() + "/build-plugin.bat");
				pb.redirectErrorStream(true);
				pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
				int batchFinished = pb.start().waitFor();
				
				if (batchFinished != 0) {
					System.out.println("Problem, pluginbuilder failed");
				}
				
				// Copy the plugin to the export folder TODO: Make it not platform specific
				File pluginFile = new File(pluginBuilderDirectory.getAbsolutePath() + "/plugins/SC-" + pluginName + ".dll");
				FileUtils.copyFile(pluginFile, new File(exportDirectory.getAbsolutePath() + "/plugins/" + pluginFile.getName()));
				buildSCCode.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
		// Copy necessary files into a folder
	}
	
	
	public boolean createLiveExportPlugin(String patchName) {
		int langPort = App.sc.getSendPort(); // Port where SCLang will start on (Should give option to set)
		
		// Create a file with all defs for each instance written explicitly
		String pluginName = "Live-" + patchName;
		File fmodDirectory = new File("C:/Program Files (x86)/FMOD SoundSystem/FMOD Studio 1.04.04");
		
		// Directory where plugin will be exported
		File exportDirectory = new File(fmodDirectory.getAbsolutePath() + "/supercollider/fmod/" + pluginName); 
		
		// Directory where plugin build tools are found
		File pluginBuilderDirectory = new File(FileHelper.getSCCodeDir() + "/pluginbuilder");
		
		// Make the export directory if it isn't found
		exportDirectory.mkdirs();
				
		File fout = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		
			
		for (ParamGroup exportGroup : App.paramGroupModel.getExportGroups()) {

			try {
				
				// Create temp plugin builder file
				File buildSCCode = File.createTempFile("buildplugin", ".scd");
				fos = new FileOutputStream(buildSCCode);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
				
				writePluginBuilderFile(bw, pluginBuilderDirectory, pluginName, langPort, exportGroup, "live_");
				
				bw.close();
							
				FileHelper.openIde(buildSCCode);
				
				// Read it into supercollider
				App.sc.sendCommand("\"" + buildSCCode.getAbsolutePath().replace("\\", "/") + "\"" + ".load.postln");
				// Wait to finish?
				
				Thread.sleep(1000);
				
				// Run the batch file, wait to finish
				ProcessBuilder pb = new ProcessBuilder(pluginBuilderDirectory.getAbsolutePath() + "/build-plugin.bat");
				pb.redirectErrorStream(true);
				pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
				int batchFinished = pb.start().waitFor();
				
				if (batchFinished != 0) {
					System.out.println("Problem, pluginbuilder failed");
				}
				
				// Copy the plugin to the export folder TODO: Make it not platform specific
				File pluginFile = new File(pluginBuilderDirectory.getAbsolutePath() + "/plugins/SC-" + pluginName + ".dll");
				FileUtils.copyFile(pluginFile, new File(exportDirectory.getAbsolutePath() + "/plugins/" + pluginFile.getName()));
				buildSCCode.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public void writePluginBuilderFile(BufferedWriter bw, File pluginBuilderDirectory, String pluginName, int langPort, ParamGroup group) {
		writePluginBuilderFile(bw, pluginBuilderDirectory, pluginName, langPort, group, "");
	}
	
	public void writePluginBuilderFile(BufferedWriter bw, File pluginBuilderDirectory, String pluginName, int langPort, ParamGroup group, String templatePrefix) {
		try {
			bw.write("JPluginBuilder.generateCode(");
			bw.write("\"" + pluginName + "\","); // or group.getName()
			bw.write(" " + langPort + ",");
			bw.newLine();

			bw.write("\t[\n");
			
			for (ParamModel model : group.getParamModels()) {
				
				Instance instance = model.getOwner();
				//instance.getDef().getType().toLowerCase()
				bw.write(String.format("\t\t[\\%s, \\%s, \\%s, \\%s, ", 
						instance.getName(), "module", instance.getID(), model.getName()));
				
				if (model.getClass() == DoubleParamModel.class) {
					DoubleParamModel param = (DoubleParamModel) model;
					bw.write(String.format("%.2f, %.2f, %.2f", 
							param.getDoubleMinimum(), param.getDoubleMaximum(), param.getObjectValue()));
				} else if (model.getClass() == IntParamModel.class) {
					IntParamModel param = (IntParamModel) model;
					bw.write(String.format("%d, %d, %d",
							param.getMinimum(), param.getMaximum(), param.getValue()));
				}
				bw.write("], ");
				bw.newLine();
			}
			bw.write("\t], "
					+ "\"" + pluginBuilderDirectory.getCanonicalPath().replace("\\", "/") + "\""
					+ ",\n" + "\"" + templatePrefix + "\""
					+ "\n);");		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public ParamGroupModel getParamGroupModel() {
		return paramGroupModel;
	}

}
