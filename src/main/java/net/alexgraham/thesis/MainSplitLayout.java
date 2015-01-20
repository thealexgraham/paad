package net.alexgraham.thesis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.BoxLayout;
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

import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.ui.RunningSynthsPanel;
import net.alexgraham.thesis.ui.SynthSelectorPanel;
import net.alexgraham.thesis.ui.SynthWindowDelegate;

public class MainSplitLayout extends JPanel implements SynthWindowDelegate {
	
	JSplitPane mainSplitPane;
	JSplitPane sideSplitPane;
	
	RunningSynthsPanel runningSynths;
	SynthSelectorPanel synthSelector;
	
	JPanel mainCardPanel;
		
	public MainSplitLayout() throws SocketException {
		setSize(1024, 800);
		setupLayout();
		
		runningSynths = new RunningSynthsPanel(this);
		synthSelector = new SynthSelectorPanel(this);
		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, runningSynths);

		add(sideSplitPane);
	}
	
	private void setupLayout() {
		setLayout(new GridLayout());
	}

	@Override
	public void selectSynth() {
		
	}

	@Override
	public void launchSynth(SynthDef synthDef) {
		runningSynths.launchSynth(synthDef);
	}
}
