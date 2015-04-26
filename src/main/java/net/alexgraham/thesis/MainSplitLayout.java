package net.alexgraham.thesis;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.scene.ParallelCamera;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.alexgraham.thesis.supercollider.SCLang.SCServerListener;
import net.alexgraham.thesis.supercollider.sync.ParallelSyncer;
import net.alexgraham.thesis.supercollider.sync.StepSyncer;
import net.alexgraham.thesis.supercollider.sync.SyncAction;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.EffectDef;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamGroup;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.FaderPanel;
import net.alexgraham.thesis.ui.SynthCardsPanel;
import net.alexgraham.thesis.ui.TreeLauncherPanel;
import net.alexgraham.thesis.ui.connectors.Connection;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.supercollider.synths.Effect;

public class MainSplitLayout extends JPanel implements SCServerListener {
	
	JSplitPane mainSplitPane;
	JSplitPane sideSplitPane;
	
	JSplitPane middleSplitPane;
	
	TreeLauncherPanel synthSelector;
	
	JPanel mainCardPanel;

	LineConnectPanel lineConnect;
	FaderPanel faderPanel;
	

	public MainSplitLayout() throws SocketException {
		
		App.sc.addUpdateListener(SCServerListener.class, this);
		setSize(1024, 800);
		setupLayout();
		
		lineConnect = new LineConnectPanel();
		synthSelector = new TreeLauncherPanel();
		
		App.data.setLineConnectPanel(lineConnect);
		
		JScrollPane scroller = new JScrollPane(lineConnect);

		
		//middleSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroller, faderPanel);
		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, scroller);
		
		
		sideSplitPane.setResizeWeight(0);
		sideSplitPane.setDividerLocation(250);
		
		sideSplitPane.setOneTouchExpandable(true);
		
		
		SynthCardsPanel cardsPanel = new SynthCardsPanel();
		cardsPanel.setMaximumSize(new Dimension(160, 100));
		lineConnect.addSynthSelectListener(cardsPanel);
		
		faderPanel = new FaderPanel();
		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cardsPanel, faderPanel);
		rightSplitPane.setDividerLocation(600);
		
		JSplitPane wholeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplitPane, rightSplitPane);
		wholeSplitPane.setResizeWeight(1);
		wholeSplitPane.setDividerLocation(1000);
		//add(sideSplitPane);
		add(wholeSplitPane);
		
		lineConnect.setFocusable(true);
		lineConnect.requestFocusInWindow();
	}
	
	@Override
	public void serverReady() {
		
		
		String mainExportName = "Main";
		ParamGroup mainExportGroup = App.paramGroupModel.getExportGroupByName(mainExportName);
		 if (mainExportGroup == null) {
			 mainExportGroup = App.paramGroupModel.newExportGroup(mainExportName);
		 }
		 

		// Create faders
		Def faderDef = App.defModel.getDefByName("fader");
		ArrayList<Effect> faders = new ArrayList<Effect>();
		

		// Need to wait for done messages
		if (faderDef != null) {
			
			StepSyncer stepSyncer = new StepSyncer();
			
			ParallelSyncer faderAddSyncer = new ParallelSyncer();
			
			Effect masterFader = new Effect(faderDef);
			masterFader.setName("Master");
			masterFader.setStartCommand("/effect/add/master"); // Master fader needs special command
			masterFader.setCloseCommand("/do/nothing");
			
			App.synthModel.addCustomInstance(masterFader);
			
			// Set master amp to the main export group
			ParamModel masterAmp = masterFader.getModelForParameterName("amp");
			masterAmp.setExportGroup(mainExportGroup);
			mainExportGroup.addParamModel(masterAmp);
			
			faderAddSyncer.addStartAction(new SyncAction() {
				@Override
				public void doAction() {
					masterFader.start();
				}
			});
			
			faderAddSyncer.addOSCListener(masterFader.getStartCommand() + "/done");

			for (int i = 1; i <= 0; i++) {
				Effect fader = new Effect(faderDef);
				fader.setName("fader" + i);
				faders.add(fader);
				
				faderAddSyncer.addStartAction(new SyncAction() {
					@Override
					public void doAction() {
						fader.start();
					}
				});
				//App.synthModel.addCustomInstance(fader);
				faderAddSyncer.addOSCListener(fader.getStartCommand() + "/done");
			}
			
			stepSyncer.addStep(faderAddSyncer);
			stepSyncer.addStep(new SyncAction() {
				
				@Override
				public void doAction() {
					for (Effect fader : faders) {
						// Connect the fader to the master fader
						Connection conn = new Connection(masterFader.getConnector(ConnectorType.AUDIO_INPUT), fader.getConnector(ConnectorType.AUDIO_OUTPUT));
						conn.connectModules();
						//App.connectionModel.addConnection(conn);
					}
					
					if (App.sc.isRebooted()){
						// Remove the previous master fader
						faderPanel.removeAll();
					}
					
					faderPanel.setup(faders, masterFader);
					faderPanel.revalidate();
					faderPanel.repaint();
//					lineConnect.addConnectablePanels(faderPanel.getConnectablePanels());
				}
			});
			
			stepSyncer.run();

		}
		

		
		
		
	}

	public void saveLineConnect() throws IOException {
    	// Write to disk with FileOutputStream
		lineConnect.saveModules();
	}
	
	public void loadLineConnect() throws IOException, ClassNotFoundException {
    	// Write to disk with FileOutputStream
		lineConnect.loadModules();
	}
	
	private void setupLayout() {
		setLayout(new GridLayout());
	}

}
