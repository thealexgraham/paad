package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.processing.Messager;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.supercollider.models.DefModel.MessageListener;

public class Synchronizer {
	public interface DefAction {
		public void doAction();
	}
	
	private DefAction startAction;
	private DefAction finalAction;
	private int actionsLeft = 0;
	
	public Synchronizer() {
		
	}
	
	public Synchronizer(DefAction startAction) {
		this.startAction = startAction;
	}
	
	public void setStartAction(DefAction startAction) {
		this.startAction = startAction;
	}
	
	public void start() {
		startAction.doAction();
	}
	
	public void addMessageListener(String message, Messenger messenger, DefAction action) {
		actionsLeft = actionsLeft + 1;
		
		messenger.addMessageListener(message, new MessageListener() {
			
			@Override
			public void doAction() {
				action.doAction();
				
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					finalAction.doAction();
				}		
			}
		});
	}
	
	public void addOSCListener(String address, DefAction action) {
		actionsLeft = actionsLeft + 1;
		
		App.sc.createListener(address, new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				action.doAction();
				
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					finalAction.doAction();
				}

			}
		});
	}
	
	/**
	 * Creates an OSC listener to synchronize to without an action
	 * @param address
	 */
	public void addOSCListener(String address) {
		App.sc.createListener(address, new OSCListener() {
			
			@Override
			public void acceptMessage(Date time, OSCMessage message) {
				// This action is finsihed, remove it
				actionsLeft = actionsLeft - 1;
				
				if (actionsLeft < 1) {
					//If theres no actions left we should be done, so do the final action
					finalAction.doAction();
				}
				
			}
		});
	}
	
	public void setFinalAction(DefAction finalAction) {
		finalAction = this.finalAction;
	}
}
