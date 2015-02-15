package net.alexgraham.thesis.supercollider.models;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.EffectDef;
import net.alexgraham.thesis.supercollider.synths.InstDef;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.SynthDef;
import net.alexgraham.thesis.ui.SynthPanel;
import net.alexgraham.thesis.ui.old.RoutinePlayerPanel;

public class SynthModel {
	
	public interface SynthModelListener {
		public void synthAdded(Synth synth);
		public void instAdded(Instrument inst);
		public void effectAdded(Effect effect);
	}
	
	private CopyOnWriteArrayList<SynthModelListener> listeners = 
			new CopyOnWriteArrayList<SynthModel.SynthModelListener>();
	
	private DefaultListModel<Synth> synthListModel 
		= new DefaultListModel<Synth>();
	
	private Hashtable<String, Synth> synths = new Hashtable<String, Synth>();
	
	
	
	public SynthModel() {

	}
	
	public void addListener(SynthModelListener l) {
		listeners.add(l);
	}
	
	public void fireSynthAdded(Synth synth) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.synthAdded(synth);
		}
	}
	
	public void fireInstAdded(Instrument inst) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.instAdded(inst);
		}
	}
	
	public void fireEffectAdded(Effect effect) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.effectAdded(effect);
		}
	}
	
	public ArrayList<Instrument> getInstruments() {
		ArrayList<Instrument> insts = new ArrayList<Instrument>();
		
		for (Enumeration<Synth> e = synthListModel.elements(); e.hasMoreElements();)  {
			Synth synth = e.nextElement();
			if (synth.getClass() == Instrument.class) {
				insts.add((Instrument) synth);
			}
		}
		
		return insts;
	}
	
	
	public void launchSynth(SynthDef synthDef) {
		
		// Create the synth and its panel
		Synth synth = new Synth(synthDef, App.sc);
		synth.start();
		
		synthListModel.addElement(synth);
		synths.put(synth.getID(), synth);
		fireSynthAdded(synth);
		
	}
	
	public void addInstrument(InstDef instDef) {
		// Create the synth and its panel
		Instrument inst = new Instrument(instDef, App.sc);

		synthListModel.addElement(inst);
		synths.put(inst.getID(), inst);
		fireInstAdded(inst);
	}

	public void addEffect(EffectDef synthDef) {
		// Create the synth and its panel
		Effect effect = new Effect(synthDef, App.sc);

		synthListModel.addElement(effect);
		synths.put(effect.getID(), effect);
		fireEffectAdded(effect);
	}
	
	
}
