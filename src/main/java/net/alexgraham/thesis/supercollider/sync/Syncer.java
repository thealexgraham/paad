package net.alexgraham.thesis.supercollider.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import net.alexgraham.thesis.supercollider.OSC;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.utility.OSCPatternAddressSelector;


public abstract class Syncer {
	
	public interface SyncFinishedListener {
		public void doAction();
	}
	
	protected ArrayList<OSCPatternAddressSelector> oscAddresses = new ArrayList<OSCPatternAddressSelector>();

	
	private CopyOnWriteArrayList<SyncFinishedListener> syncFinishedListeners =
			new CopyOnWriteArrayList<Syncer.SyncFinishedListener>();
	
	
	public void addSyncFinishedListener(SyncFinishedListener syncFinishedListener) {
		syncFinishedListeners.add(syncFinishedListener);
	}
	
	public void removeSyncFinishedListener(SyncFinishedListener listener) {
		syncFinishedListeners.remove(listener);
	}
	
	public void fireSyncerFinished() {
		for (SyncFinishedListener syncFinishedListener : syncFinishedListeners) {
			syncFinishedListener.doAction();
		}
	}
	
	public void run() {
		
	}
	
	public void close() {
		fireSyncerFinished();
	}
	
	private void clearOSCListeners() {
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

}
