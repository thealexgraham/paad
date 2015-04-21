package net.alexgraham.thesis.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.models.SynthModel.SynthModelListener;
import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.Synth.SynthListener;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel.SynthSelectListener;

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
	
	// Interface implementations //
	///////////////////////////////
	
	// SynthSelectListener
	// ----------------------
	public void selectSynth(Synth synth) {
		// Get the card layout and show the correct new card
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, synth.getID());
	}
	
	public void deselectSynth() {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, "No Synth Selected");
	}
	
	@Override
	public void selectInstance(Instance instance) {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, instance.getID());
	}

	@Override
	public void deselectInstance() {
		CardLayout c1 = (CardLayout)(selectedSynthPanel.getLayout());
		c1.show(selectedSynthPanel, "No Module Selected");
	}



	
	// SynthListener
	// ------------------
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

	
	// SynthModelListener
	// ------------------------
	
	// Currently all of these use the same panel, so just do it in the helper function
	@Override
	public void synthAdded(Synth synth) {
		addPanelForSynth(synth);
	}

	@Override
	public void instAdded(Instrument inst) {
		addPanelForSynth(inst);
	}

	@Override
	public void effectAdded(Effect effect) {
		addPanelForSynth(effect);
	}
	
	@Override
	public void changeFuncAdded(ChangeFunc changeFunc) {
		addPanelForSynth(changeFunc);
		
	}
	
	private void addPanelForSynth(Synth synth) {
		synth.addSynthListener(this);
		
		// Create the SynthPanel and add it to the list of cards
		SynthPanel panel = new SynthPanel(synth);
		selectedSynthPanel.add(panel, synth.getID());
	}
	
	private void addPanelForInstance(Instance instance) {
		
		// Create the SynthPanel and add it to the list of cards
		InstancePanel panel = new InstancePanel(instance);
		selectedSynthPanel.add(panel, instance.getID());
	}

	@Override
	public void patternGenAdded(PatternGen patternGen) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void instanceAdded(Instance instance) {
		// TODO Auto-generated method stub
//		if (instance instanceof Synth) {
//			addPanelForSynth((Synth) instance);
//		}
		addPanelForInstance(instance);
	}




}
