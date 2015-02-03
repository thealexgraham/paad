package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.SocketException;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.InstDef;
import net.alexgraham.thesis.supercollider.Instrument;
import net.alexgraham.thesis.supercollider.RoutinePlayer;
import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.Synth.SynthListener;
import net.alexgraham.thesis.supercollider.SynthDef;
import net.alexgraham.thesis.supercollider.SynthModel.SynthModelListener;
import net.alexgraham.thesis.ui.SynthInfoList.SynthSelectListener;

public class RunningSynthsPanel extends JPanel implements SynthSelectListener, SynthListener, SynthModelListener {
		
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
		
		App.synthModel.addListener(this);
		
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
	
	public void selectSynth(Synth synth) {
		// Get the card layout and show the correct new card
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, synth.getID());
	}
	
	public void deselectSynth() {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, "No Synth Selected");
	}
	
	public void newPlayerWindow(JPanel panel) {
		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // JFrame Test
				JFrame frame = new JFrame() {
					public void dispose() {
						super.dispose();
					}
				};
				frame.add(panel);
				frame.setTitle("Test Routine Player");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
            }
        });

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

	@Override
	public void synthAdded(Synth synth) {
		synth.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		selectedSynthPanel.add(panel, synth.getID());
		synthInfoList.addSynthInfoPanel(synth);
	}

	@Override
	public void instAdded(Instrument inst) {
		// TODO Auto-generated method stub
		
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
	
	public void addInstrument(InstDef instDef) {
		// Create the synth and its panel
		Instrument synth = new Instrument(instDef, App.sc);
//		synth.start();
		synth.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		selectedSynthPanel.add(panel, synth.getID());
		
		synths.put(synth.getID(), synth);
		
		synthInfoList.addSynthInfoPanel(synth);
		
		RoutinePlayer player = new RoutinePlayer();
		RoutinePlayerPanel playerPanel = new RoutinePlayerPanel(player);
		
		newPlayerWindow(playerPanel);

		player.connectInstrument(synth);
		//player.play();
		
		//player.play();
		//App.sc.sendMessage("/inst/playtest", synth.getName(), synth.getID());
		//synth.runInstrumentTest();
		
		//newSynthWindow(synth);
	}
	

}
