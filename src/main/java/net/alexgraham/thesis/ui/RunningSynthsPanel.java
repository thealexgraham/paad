package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Console;
import java.util.AbstractList;
import java.util.Hashtable;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.supercollider.Synth.SynthListener;
import net.alexgraham.thesis.ui.SynthInfoList.SynthSelectListener;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.ResizeCardLayout;

public class RunningSynthsPanel extends JPanel implements SynthSelectListener, SynthListener {
		
	JList<String> synthList;
	DefaultListModel<String> synthListModel;
	
	SynthInfoList synthInfoList;
	
	JSplitPane splitPane;

	
	JPanel synthListPanel;
	JPanel selectedSynthPanel;
	
	JPanel selectedContainer;
	
	Hashtable<String, Synth> synths;
	
	private int synthCount = 0;

	
	public RunningSynthsPanel () {
		// So it resizes
		setSize(300, 150);
		setLayout(new GridLayout());
		
		synths = new Hashtable<String, Synth>();
			
		// Setup Synth List Panel
		setupSynthList();
		
		// Setup main panel //
		setupSelectedSynthPanel();
		
		// Setup Split Window //
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, synthListPanel, selectedSynthPanel);
		add(splitPane);
		
		//add(synthListPanel);
	}
	
	private void setupSynthList() {

		synthListPanel = new JPanel();
		synthListPanel.setLayout(new BoxLayout(synthListPanel, BoxLayout.Y_AXIS));
		synthListPanel.setBackground(Color.DARK_GRAY);
		
		JLabel label = new JLabel("Running Instruments");
		label.setForeground(Color.white);
		label.setAlignmentX(CENTER_ALIGNMENT);
		
		synthListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		synthListPanel.add(label);
		synthListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		synthInfoList = new SynthInfoList();
		synthInfoList.addListener(this);
		
		JScrollPane listScrollPane = new JScrollPane(synthInfoList);
		synthListPanel.add(listScrollPane);
		listScrollPane.setMinimumSize(SynthInfoPanel.getDefaultSize());		
	}

	private void setupSelectedSynthPanel() {
		selectedSynthPanel = new JPanel(new CardLayout());
		JPanel defaultCard = new JPanel();
		defaultCard.add(new JLabel("No Synth Selected"), BorderLayout.CENTER);
		selectedSynthPanel.add(defaultCard, "No Synth Selected");
	}
	
	public void launchSynth(SynthDef synthDef) {
		
		// Create the synth and its panel
		Synth synth = new Synth(synthDef, App.sc);
		synth.start();
		synth.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		selectedSynthPanel.add(panel, synth.getID());
		
		synths.put(synth.getID(), synth);
		
		synthInfoList.addSynthInfoPanel(synth);

		
		//newSynthWindow(synth);
	}
	
	public void selectSynth(Synth synth) {
		// Get the card layout and show the correct new card
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, synth.getID());
	}
	
	public void deselectSynth() {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, "No Synth Selected");
	}
	
	public void newSynthWindow(Synth synth) {
		// JFrame Test
		JFrame frame = new JFrame() {
			public void dispose() {
				super.dispose();
				synth.close();
			}
		};
		frame.add(new SynthInfoPanel(synth));
		frame.setTitle(synth.getSynthName());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	// SynthListener
	// ------------------------
	@Override
	public void synthClosed(Synth synth) {

		// Remove synth from info list
		synthInfoList.removeSynth(synth);

		// Remove synth from panels
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		for (Component comp : selectedSynthPanel.getComponents()) {
			// Make sure it isn't the default panel
			if (comp.getClass().equals(SynthPanel.class)) {
				SynthPanel panel = (SynthPanel) comp;
				// If this is the right one, delete it
				if (panel.getSynth() == synth) {
					c1.removeLayoutComponent(panel);
				}
			}
		}

	}
	
	@Override
	public void parameterChanged(String paramName, double value) {

	}

}
