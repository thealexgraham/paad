package net.alexgraham.thesis.supercollider.models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.players.PatternPlayer;
import net.alexgraham.thesis.supercollider.sync.ParallelSyncer;
import net.alexgraham.thesis.supercollider.sync.StepSyncer;
import net.alexgraham.thesis.supercollider.sync.SyncAction;
import net.alexgraham.thesis.supercollider.sync.Syncer;
import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.Chooser;
import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instance;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.SpecialAction;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.TaskPlayer;
import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.supercollider.synths.defs.ChangeFuncDef;
import net.alexgraham.thesis.supercollider.synths.defs.ChooserDef;
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
	
	public ArrayList<PatternPlayer> getPlayers() {
		ArrayList<PatternPlayer> players = new ArrayList<PatternPlayer>();
		
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance synth = e.nextElement();
			if (synth.getClass() == PatternPlayer.class) {
				players.add((PatternPlayer) synth);
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
		
	/**
	 * Refreshes instances that should already be running in memory. This will update the definitions
	 * to SuperCollider, start the synths and refresh the models
	 * 
	 * @return returns a Syncer that the calling function is responsible for running
	 */
	public Syncer refreshInstances() {
	
		StepSyncer stepSync = new StepSyncer();

		ParallelSyncer definitionsSyncer = new ParallelSyncer();
		stepSync.addStep(new SyncAction() {
			
			@Override
			public void doAction() {
				//As steps: 
				// step 1: load definitions of instances (Paralell)

				for (Instance instance : getInstances()) {
					
					// Don't need to do this for routine player, since there's no definition
					
					definitionsSyncer.addStartAction(new SyncAction() {
						@Override
						public void doAction() {
							// Load the DefFile
							File defFile = instance.getDef().createFileDef();
							App.sc.sendCommand("\"" + defFile.getAbsolutePath().replace("\\", "/") + "\"" + ".load.postln");
							defFile.deleteOnExit();
						}
					});
					
					// Make sure the def is in loaded in SuperCollider before continuing
					definitionsSyncer.addOSCListener("/def/ready/" + instance.getDefName());
				}
				
				definitionsSyncer.run();
			}
		}, definitionsSyncer);
	
		// step 2: Start all instances (paralell)
		ParallelSyncer startInstanceSyncer = new ParallelSyncer();
		stepSync.addStep(new SyncAction() {
			@Override
			public void doAction() {
				for (Instance instance : getInstances()) {
					
					if (instance.getName().equals("Master"))
						continue;
					
					instance.createNewID();
					
					// Start each synth in paralell					
					startInstanceSyncer.addStartAction(new SyncAction() {
						@Override
						public void doAction() {
							instance.start();
							instance.refreshModels();
							
							// Fire instance added for everyone
							for (SynthModelListener synthModelListener : listeners) {
									synthModelListener.instanceAdded(instance);
							}
						}
					});
					System.out.println("Adding listener for " + instance.getName());
					startInstanceSyncer.addOSCListener(instance.getStartCommand() + "/done");
				}
				
				// Begin starting the instances
				startInstanceSyncer.run();
			}
		}, startInstanceSyncer);
		
		
		return stepSync;
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
		
		switch (type.toLowerCase()) {
			case "synth":
				instance = new Synth(def);
				break;
			case "instrument":
				instance = new Instrument(def);
				break;
			case "effect":
				instance = new Effect(def);
				break;
			case "changefunc":
				instance = new ChangeFunc(def);
				break;
			case "patterngen":
				instance = new PatternGen(def);
				break;
			case "chooser":
				instance = new Chooser(def);
				break;
			case "taskrunner":
				instance = new TaskRunner(def);
				break;
			case "taskplayer":
				instance = new TaskPlayer(def);
				break;
			case "patternplayer":
				instance = new PatternPlayer(def);
				break;
			case "specialaction":
				instance = new SpecialAction(def);
				break;
			default:
				System.err.println("Could not find any instance type for " + type);
				return;
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
		
		if (!instance.getName().equals("Master")) {
			fireInstanceAdded(instance);			
		}

	}
	
	/**
	 * Like addInstance but does not notify SynthModelListeners or start the instance
	 * Used for app defined instances (ie faders)
	 * @param instance
	 */
	public void addCustomInstance(Instance instance) {
		synthListModel.addElement(instance);
		synths.put(instance.getID(), instance);
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
	
	
	public void refreshInstancesOld() {
		for (Enumeration<Instance> e = synthListModel.elements(); e.hasMoreElements();)  {
			Instance instance = (Instance) e.nextElement();
			
//			if (instance.getClass() == RoutinePlayer.class) {
//				App.playerModel.addPlayer((RoutinePlayer) instance);
//				continue;
//			}
			instance.start();
			instance.refreshModels();

			// Fire instance added for everyone
			for (SynthModelListener synthModelListener : listeners) {
					synthModelListener.instanceAdded(instance);
			}
		}
	}
}
