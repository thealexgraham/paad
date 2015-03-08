package net.alexgraham.thesis.supercollider.sync;

import java.util.ArrayList;
import java.util.Date;

import net.alexgraham.thesis.supercollider.Messenger;
import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.models.DefModel.MessageListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.utility.OSCPatternAddressSelector;

public class ParallelSyncer extends Syncer{
	
	private ArrayList<SyncAction> startActions = new ArrayList<SyncAction>();
	private ArrayList<SyncAction> finishActions = new ArrayList<SyncAction>();
	
	private int actionsLeft = 0;
	
	public void addStartAction(SyncAction startAction) {
		startActions.add(startAction);
	}
	public void addFinishAction(SyncAction finishAction) {
		finishActions.add(finishAction);
	}

	
	public void addOSCListener(String address) {
		addOSCListener(address, null);
	}
	public void addOSCListener(String address, SyncAction finishAction) {
		// Add this listener to the queue
		addListener();
		
		OSCListener listener = new OSCListener() {
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				if (finishAction != null) 
					finishAction.doAction();
				
				// Remove this listener from the queue
				listenerFinished();
				
				// Remove the OSC Listener
				OSC.removeListener(this);
			}
		};
		
		OSC.addListener(address, listener);
	}

	public void addSyncerFinish(Syncer syncer) {
		// Add a syncerFinish with no 
		addSyncerFinish(syncer, null);
	}
	public void addSyncerFinish(Syncer syncer, SyncAction finishAction) {
		addListener();
		syncer.addSyncFinishedListener(new SyncFinishedListener() {
			@Override
			public void doAction() {
				if (finishAction != null) 
					finishAction.doAction();
				
				// Remove this listener from the queue
				listenerFinished();
			}
		});
	}
	
	public void addMessageListener(String message, Messenger messenger, SyncAction finishAction) {
		addListener();
		
		MessageListener listener = new MessageListener() {
			@Override
			public void doAction() {
				finishAction.doAction();
				listenerFinished();
				
				// Remove the message listener
				messenger.removeMessageListener(this);
			}
		};
		
		messenger.addMessageListener(message, listener);
	}
	
	public void addListener() {
		actionsLeft = actionsLeft + 1;
	}
	
	public void listenerFinished() {
		// This action is finsihed, remove it
		actionsLeft = actionsLeft - 1;
		
		if (actionsLeft < 1) {
			//If theres no actions left we should be done, so do the final action
			finish();
		}
	}
	
	@Override
	public void start() {
		// Do all start actions
		for (SyncAction syncAction : startActions) {
			syncAction.doAction();
		}
	}
	
	public void finish() {
		// Do all start actions
		for (SyncAction syncAction : finishActions) {
			syncAction.doAction();
		}
		
		close();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
}
