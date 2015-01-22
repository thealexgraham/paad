package net.alexgraham.thesis.ui.old;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.ui.SynthInfoPanel.SynthInfoPanelDelegate;
import net.alexgraham.thesis.ui.SynthPanel.SynthPanelDelegate;
import net.alexgraham.thesis.ui.components.ResizeCardLayout;

public class RunningSynthsPanelList extends JPanel implements SynthPanelDelegate, SynthInfoPanelDelegate {
		
	JList<String> synthList;
	DefaultListModel<String> synthListModel;
	
	JSplitPane splitPane;

	
	JPanel synthListPanel;
	JPanel selectedSynthPanel;
	
	JPanel selectedContainer;
	
	Hashtable<String, Synth> synths;
	
	private int synthCount = 0;
	
	SynthWindowDelegate delegate;
	
	public RunningSynthsPanelList (SynthWindowDelegate delegate) {
		this.delegate = delegate;

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
		
		JLabel label = new JLabel("Running Instruments");
		label.setAlignmentX(CENTER_ALIGNMENT);
		synthListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		synthListPanel.add(label);
		synthListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		synthListModel = new DefaultListModel<String>();
		synthList = new JList<String>(synthListModel);
		synthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane listScrollPane = new JScrollPane(synthList);
		synthListPanel.add(listScrollPane);
		
		synthList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				JList theList = (JList)e.getSource();
				if (theList.getSelectedIndex() != -1) {
					
					// Get the card layout and show the correct new card
					CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
					Synth theSynth = synths.get(synthList.getSelectedValue());
					c1.show(selectedSynthPanel, theSynth.getID());
					
//					// Resize the top frame
//					JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(RunningSynthsPanel.this);
//					topFrame.pack();
				}
			}
		});
		
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
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		panel.addDelegate(this);
		selectedSynthPanel.add(panel, synth.getID());
		
		synths.put(synth.getID(), synth);
		synthListModel.addElement(synth.getID());
		
		newSynthWindow(synth);
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
	
	
	// Delegate Methods
	@Override
	public void synthClosed(Synth synth, SynthPanel panel) {
		synthListModel.removeElement(synth.getID());
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.removeLayoutComponent(panel);
	}

	@Override
	public void synthClosed(Synth synth, SynthInfoPanel panel) {
		
		
	}
	

}
