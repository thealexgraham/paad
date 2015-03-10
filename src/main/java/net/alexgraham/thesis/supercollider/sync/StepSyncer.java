package net.alexgraham.thesis.supercollider.sync;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.sync.Syncer.SyncFinishedListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.utility.OSCPatternAddressSelector;
import com.sun.corba.se.spi.activation._ActivatorImplBase;

/**
 * @author Alex
 * Class that allows for synchronization in steps
 * Generally a step consists of an Action to perform and a listener
 * that must be fulfilled before the next action is run
 */
public class StepSyncer extends Syncer {
	private class SyncStep {
		public SyncAction action;
	}
	
	private class ActionStep extends SyncStep {
		public ActionStep(SyncAction action) {
			this.action = action;
		}
	}
	
	private class OSCStep extends SyncStep {
		public String verification;
		public OSCStep(SyncAction action, String verification) {
			this.action = action;
			this.verification = verification;
		}
	}
	
	private class SyncerStep extends SyncStep {
		public Syncer syncer;
		public SyncerStep(SyncAction action, Syncer syncer) {
			this.action = action;
			this.syncer = syncer;
		}
	}
	
	// Maybe types would be better with one big class
	
	private ArrayList<OSCPatternAddressSelector> oscAddresses = new ArrayList<OSCPatternAddressSelector>();
	private ArrayList<SyncStep> steps = new ArrayList<SyncStep>(); 
	
	/**
	 * Perform the action and move on to the next step without waiting
	 * @param action
	 */
	public void addStep(SyncAction action) {
		steps.add(new ActionStep(action));
	}
	
	public void addStep(SyncAction action, String verification) {
		steps.add(new OSCStep(action, verification));
	}
	
	public void addStep(SyncAction action, Syncer syncer) {
		steps.add(new SyncerStep(action, syncer));
	}
	
	/**
	 * With no action, this will just run the syncer when the step is performed
	 * @param syncer
	 */
	public void addStep(Syncer syncer) {
		steps.add(new SyncerStep(new SyncAction() {
			@Override
			public void doAction() {
				// TODO Auto-generated method stub
				syncer.run();
			}
		}, syncer));
	}
	
	@Override
	public void close() {
		// Overwrite the listeners with blank ones
		for (OSCPatternAddressSelector addressSelector : oscAddresses) {
			OSC.addListener(addressSelector, new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					// Do nothing
				}
			});
		}
		
		super.close();
	}
	
	public void run() {
		nextAction();
	}
	
	public void nextAction() {
		// Get and remove the next action out of the array list	
		
		// If array lists aren't empty
		if (steps.size() > 0) {
			SyncStep step = steps.remove(0); // Get the next step in line
			performAction(step); // Perform the action and set up the listener
		} else {
			// Otherwise, close (and/or do final action)
			close();
		}
	}
	
	public void performAction(SyncStep step) {
		SyncAction action = step.action;

		
		if (step.getClass() == ActionStep.class) {
			// If there's no verification, do the action and move on
			action.doAction();
			nextAction();
			return;
		} else if (step.getClass() == OSCStep.class) {
			
			String verification = ((OSCStep) step).verification;
			
			OSCPatternAddressSelector selector = new OSCPatternAddressSelector(verification);
			oscAddresses.add(selector);
			
			// When this action's verification has happened, set up the next action
			OSC.addListener(selector, new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					nextAction();
				}
			});
			
		} else if (step.getClass() == SyncerStep.class) {
			Syncer syncer = ((SyncerStep) step).syncer;
			syncer.addSyncFinishedListener(new SyncFinishedListener() {
				@Override
				public void doAction() {
					nextAction();
				}
			});
		}
		
		action.doAction();
	}
}
