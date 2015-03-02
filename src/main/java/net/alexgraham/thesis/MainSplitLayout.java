package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.xml.stream.events.StartDocument;

import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.EffectDef;
import net.alexgraham.thesis.supercollider.synths.defs.InstDef;
import net.alexgraham.thesis.ui.SynthCardsPanel;
import net.alexgraham.thesis.ui.SynthLauncherPanel;
import net.alexgraham.thesis.ui.TreeLauncherPanel;
import net.alexgraham.thesis.ui.SynthLauncherPanel.SynthLauncherDelegate;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.macstyle.RunningSynthsPanel;
import net.alexgraham.thesis.ui.macstyle.SynthInfoList;
import net.alexgraham.thesis.ui.old.SynthWindowDelegate;

public class MainSplitLayout extends JPanel implements SynthLauncherDelegate {
	
	JSplitPane mainSplitPane;
	JSplitPane sideSplitPane;
	
	RunningSynthsPanel runningSynths;
	TreeLauncherPanel synthSelector;
	
	JPanel mainCardPanel;
	private JButton testButton;
	private JButton otherButton;
	
	LineConnectPanel lineConnect;
	

	public MainSplitLayout() throws SocketException {
		setSize(1024, 800);
		setupLayout();
		
		lineConnect = new LineConnectPanel();
		synthSelector = new TreeLauncherPanel();
		
		App.data.setLineConnectPanel(lineConnect);
		
		JScrollPane scroller = new JScrollPane(lineConnect);
		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, scroller);

		
		sideSplitPane.setResizeWeight(0.8);
		sideSplitPane.setDividerLocation(250);
		
		sideSplitPane.setOneTouchExpandable(true);
		
		
		SynthCardsPanel cardsPanel = new SynthCardsPanel();
		cardsPanel.setMaximumSize(new Dimension(160, 100));
		lineConnect.addSynthSelectListener(cardsPanel);
		
		JSplitPane wholeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplitPane, cardsPanel);
		wholeSplitPane.setDividerLocation(1000);
		//add(sideSplitPane);
		add(wholeSplitPane);
		
		lineConnect.setFocusable(true);
		lineConnect.requestFocusInWindow();
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

	@Override
	public void launchSynth(Def def) {
		runningSynths.launchSynth(def);
	}

	@Override
	public void addInstrument(InstDef instdef) {
		
		runningSynths.addInstrument(instdef);
		
	}

	@Override
	public void addEffect(EffectDef effectDef) {
		
		
	}
}
