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
import net.alexgraham.thesis.supercollider.models.SynthModel.SynthModelListener;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.InstDef;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.SynthDef;
import net.alexgraham.thesis.supercollider.synths.Synth.SynthListener;
import net.alexgraham.thesis.ui.SynthInfoList.SynthSelectListener;

public class SynthCardsPanel extends JPanel implements SynthSelectListener, SynthListener, SynthModelListener {
		
	JPanel selectedSynthPanel;
	
	JPanel selectedContainer;
	
	Hashtable<String, Synth> synths;
	
	public SynthCardsPanel () {
		// So it resizes
		setSize(300, 150);
		setLayout(new GridLayout());
		
		App.synthModel.addListener(this);
		
		synths = new Hashtable<String, Synth>();

		
		// Setup main panel //
		setupSelectedSynthPanel();
		
		add(selectedSynthPanel);
		//add(synthListPanel);
	}

	private void setupSelectedSynthPanel() {
		selectedSynthPanel = new JPanel(new CardLayout());
		JPanel defaultCard = new JPanel();
		defaultCard.add(new JLabel("No Synth Selected"), BorderLayout.CENTER);
		selectedSynthPanel.add(defaultCard, "No Synth Selected");
	}
	
	// SynthSelectListener
	// -----------------------
	public void selectSynth(Synth synth) {
		// Get the card layout and show the correct new card
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, synth.getID());
	}
	
	public void deselectSynth() {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, "No Synth Selected");
	}

	
	// SynthListener
	// ------------------------
	@Override
	public void synthClosed(Synth synth) {

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
		System.out.println("Adding Synth");
		synth.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		selectedSynthPanel.add(panel, synth.getID());
	}

	@Override
	public void instAdded(Instrument inst) {

		inst.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(inst);
		selectedSynthPanel.add(panel, inst.getID());
	}


}
