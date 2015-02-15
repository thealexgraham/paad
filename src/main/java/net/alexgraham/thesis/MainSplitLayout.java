package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
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

import net.alexgraham.thesis.supercollider.synths.EffectDef;
import net.alexgraham.thesis.supercollider.synths.InstDef;
import net.alexgraham.thesis.supercollider.synths.SynthDef;
import net.alexgraham.thesis.ui.SynthCardsPanel;
import net.alexgraham.thesis.ui.SynthLauncherPanel;
import net.alexgraham.thesis.ui.SynthLauncherPanel.SynthLauncherDelegate;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;
import net.alexgraham.thesis.ui.macstyle.RunningSynthsPanel;
import net.alexgraham.thesis.ui.macstyle.SynthInfoList;
import net.alexgraham.thesis.ui.old.SynthWindowDelegate;

public class MainSplitLayout extends JPanel implements SynthLauncherDelegate {
	
	JSplitPane mainSplitPane;
	JSplitPane sideSplitPane;
	
	RunningSynthsPanel runningSynths;
	SynthLauncherPanel synthSelector;
	
	JPanel mainCardPanel;
	private JButton testButton;
	private JButton otherButton;
	

	public MainSplitLayout() throws SocketException {
		setSize(1024, 800);
		setupLayout();
		
//		runningSynths = new RunningSynthsPanel();
		LineConnectPanel lineConnect = new LineConnectPanel();
		synthSelector = new SynthLauncherPanel(this);

		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, lineConnect);
		
		sideSplitPane.setOneTouchExpandable(true);
		
		
		SynthCardsPanel cardsPanel = new SynthCardsPanel();
		lineConnect.addSynthSelectListener(cardsPanel);
		
		JSplitPane wholeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplitPane, cardsPanel);
		
		//add(sideSplitPane);
		add(wholeSplitPane);
		
		lineConnect.setFocusable(true);
		lineConnect.requestFocusInWindow();
	}
	
	private void setupLayout() {
		setLayout(new GridLayout());
	}

	@Override
	public void launchSynth(SynthDef synthDef) {
		runningSynths.launchSynth(synthDef);
	}

	@Override
	public void addInstrument(InstDef instdef) {
		
		runningSynths.addInstrument(instdef);
		
	}

	@Override
	public void addEffect(EffectDef effectDef) {
		
		
	}
}
