package net.alexgraham.thesis.supercollider.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.annotation.processing.Messager;

import net.alexgraham.thesis.supercollider.Messenger;
import net.alexgraham.thesis.supercollider.OSC;
import net.alexgraham.thesis.supercollider.models.DefModel.MessageListener;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.utility.OSCPatternAddressSelector;
import com.sun.xml.internal.ws.Closeable;

public class Synchronizer {

	private SyncAction startAction;
	private SyncAction finalAction;
	private int actionsLeft = 0;
	
	private Hashtable<MessageListener, Messenger> messageListeners = new Hashtable<MessageListener, Messenger>();
	private ArrayList<OSCPatternAddressSelector> oscAddresses = new ArrayList<OSCPatternAddressSelector>();
	
	public Synchronizer() {

	}
	
	public Synchronizer(SyncAction startAction) {
		this.startAction = startAction;
	}
	
	public void setStartAction(SyncAction startAction) {
		this.startAction = startAction;
	}
	
	public void start() {
		startAction.doAction();
	}
	
	public void stop() {
		System.out.println("Stopping");
		for (MessageListener messageListener : messageListeners.keySet()) {
			Messenger messenger = messageListeners.get(messageListener);
			messenger.removeMessageListener(messageListener);
		}
		
		// Overwrite the listeners with blank ones
		for (OSCPatternAddressSelector addressSelector : oscAddresses) {
			OSC.addListener(addressSelector, new OSCListener() {
				
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					// Do nothing
				}
			});
		}
	}
	
	public void addMessageListener(String message, Messenger messenger, SyncAction action) {
		actionsLeft = actionsLeft + 1;
		MessageListener listener = new MessageListener() {
			
			@Override
			public void doAction() {
				action.doAction();
				
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					performFinalAction();
				}		
			}
		};
		
		messenger.addMessageListener(message, listener);
		messageListeners.put(listener, messenger); // Keep track of listener so we can remove it later

	}
	
	public void addOSCListener(String address, SyncAction action) {
		actionsLeft = actionsLeft + 1;
		OSCListener listener = new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				action.doAction();
				
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					performFinalAction();
				}

			}
		};
		
		OSCPatternAddressSelector addressSelector = new OSCPatternAddressSelector(address);
		OSC.addListener(addressSelector, listener);
		oscAddresses.add(addressSelector); // Keep track of listener so we can remove it later
	}
	
	/**
	 * Creates an OSC listener to synchronize to without an action
	 * @param address
	 */
	public void addOSCListener(String address) {
		actionsLeft = actionsLeft + 1;
		OSCListener listener = new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					performFinalAction();
				}
				
			}
		};
		
		OSCPatternAddressSelector addressSelector = new OSCPatternAddressSelector(address);
		OSC.addListener(addressSelector, listener);
		oscAddresses.add(addressSelector); // Keep track of listener so we can remove it later
	}
	
	private void performFinalAction() {
		finalAction.doAction();
		this.stop();
	}
	
	public void setFinalAction(SyncAction finalAction) {
		this.finalAction = finalAction;
	}
}
