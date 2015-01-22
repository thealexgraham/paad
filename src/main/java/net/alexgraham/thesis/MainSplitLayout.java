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

import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.ui.RunningSynthsPanel;
import net.alexgraham.thesis.ui.SynthLauncherPanel;
import net.alexgraham.thesis.ui.SynthWindowDelegate;
import net.alexgraham.thesis.ui.SynthLauncherPanel.SynthLauncherDelegate;

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
		
		runningSynths = new RunningSynthsPanel();
		synthSelector = new SynthLauncherPanel(this);
		
		sideSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				synthSelector, runningSynths);
		
		add(sideSplitPane);
	}
	
	private void setupLayout() {
		setLayout(new GridLayout());
	}

	@Override
	public void launchSynth(SynthDef synthDef) {
		runningSynths.launchSynth(synthDef);
	}
}
