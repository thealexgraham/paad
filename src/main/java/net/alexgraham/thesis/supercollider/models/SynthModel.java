package net.alexgraham.thesis.supercollider.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.players.RoutinePlayer;
import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.defs.ChangeFuncDef;
import net.alexgraham.thesis.supercollider.synths.defs.Def;
import net.alexgraham.thesis.supercollider.synths.defs.EffectDef;
import net.alexgraham.thesis.supercollider.synths.defs.InstDef;
import net.alexgraham.thesis.supercollider.synths.defs.PatternGenDef;
import net.alexgraham.thesis.supercollider.synths.defs.SynthDef;
import net.alexgraham.thesis.ui.connectors.LineConnectPanel;

public class SynthModel implements Serializable {
	
	public interface SynthModelListener {
		public void synthAdded(Synth synth);
		public void instAdded(Instrument inst);
		public void effectAdded(Effect effect);
		public void changeFuncAdded(ChangeFunc changeFunc);
		public void patternGenAdded(PatternGen patternGen);
		
		public void instanceAdded(Instance instance);
	}
	
	transient CopyOnWriteArrayList<SynthModelListener> listeners = 
			new CopyOnWriteArrayList<SynthModel.SynthModelListener>();

	private DefaultListModel<Instance> synthListModel 
		= new DefaultListModel<Instance>();
	
	private Hashtable<String, Instance> synths = new Hashtable<String, Instance>();
	
	
	public void setSynthListModel(DefaultListModel<Instance> synthListModel) {
		this.synthListModel = synthListModel;
	}
	
	public SynthModel() {
		listeners = 
				new CopyOnWriteArrayList<SynthModel.SynthModelListener>();
	}
	
	public ArrayList<Instance> getInstances() {
		ArrayList<Instance> list = new ArrayList<Instance>();
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance instance = (Instance) e.nextElement();
			list.add(instance);
		}
		return list;
	}
	
	public void addListener(SynthModelListener l) {
		listeners.add(l);
	}
	
	
	public void fireInstanceAdded(Instance instance) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.instanceAdded(instance);
		}
	}
		
	public ArrayList<Instrument> getInstruments() {
		ArrayList<Instrument> insts = new ArrayList<Instrument>();
		
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance synth = e.nextElement();
			if (synth.getClass() == Instrument.class) {
				insts.add((Instrument) synth);
			}
		}
		
		return insts;
	}
	
	public ArrayList<RoutinePlayer> getPlayers() {
		ArrayList<RoutinePlayer> players = new ArrayList<RoutinePlayer>();
		
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance synth = e.nextElement();
			if (synth.getClass() == RoutinePlayer.class) {
				players.add((RoutinePlayer) synth);
			}
		}
		
		return players;
	}
	
	public ArrayList<Synth> getSynths() {
		ArrayList<Synth> synths = new ArrayList<Synth>();
		
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance synth = e.nextElement();
			if (synth.getClass() == Synth.class) {
				synths.add((Synth) synth);
			}
		}
		
		return synths;
	}
		
	public void refreshInstances() {

		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance instance = (Instance) e.nextElement();
			
//			if (instance.getClass() == RoutinePlayer.class) {
//				App.playerModel.addPlayer((RoutinePlayer) instance);
//				continue;
//			}
			instance.start();
			instance.refresh();

			// Fire instance added for everyone
			for (SynthModelListener synthModelListener : listeners) {
					synthModelListener.instanceAdded(instance);
			}

		}
	}
	
	public void closeInstances() {
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance instance = (Instance) e.nextElement();
			instance.close();

			// Fire instance added for everyone
			for (SynthModelListener synthModelListener : listeners) {
					synthModelListener.instanceAdded(instance);
			}

		}
	}

	

	public void removeInstance(Instance instance) {
		synthListModel.removeElement(instance);
		
	}
	
	public void addInstance(Def def) {
		String type = def.getType();
		Instance instance = null;
		
		switch (type) {
			case "synth":
				instance = new Synth(def);
				break;
			case "instrument":
				instance = new Instrument(def);
				break;
			case "effect":
				instance = new Effect(def);
				break;
			case "changeFunc":
				instance = new ChangeFunc(def);
				break;
			case "patternGen":
				instance = new PatternGen(def);
				break;
			default:
				break;
		}
		instance.start();
		
		synthListModel.addElement(instance);
		synths.put(instance.getID(), instance);
//		fireSynthAdded(synth);
		fireInstanceAdded(instance);
	}
	
	public void addInstance(Instance instance) {

		instance.start();
		
		synthListModel.addElement(instance);
		synths.put(instance.getID(), instance);
//		fireSynthAdded(synth);
		fireInstanceAdded(instance);
	}
	
	
	//Old
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
	
	public void fireChangeFuncAdded(ChangeFunc changeFunc) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.changeFuncAdded(changeFunc);
		}
	}
	
	public void firePatternGenFuncAdded(PatternGen patternGen) {
		for (SynthModelListener synthModelListener : listeners) {
			synthModelListener.patternGenAdded(patternGen);
		}
	}
}
