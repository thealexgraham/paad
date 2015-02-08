package net.alexgraham.thesis.supercollider;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.App;
import net.alexgraham.thesis.ui.RoutinePlayerPanel;
import net.alexgraham.thesis.ui.SynthPanel;

public class PlayerModel {
	
	public interface PlayerModelListener {
		public void playerAdded(RoutinePlayer player);
		public void playerRemoved(RoutinePlayer player);
	}
	
	private CopyOnWriteArrayList<PlayerModelListener> listeners = 
			new CopyOnWriteArrayList<PlayerModelListener>();
	
	private DefaultListModel<RoutinePlayer> playerListModel
		= new DefaultListModel<RoutinePlayer>();
	
	public PlayerModel() {

	}
	
	public void addListener(PlayerModelListener l) {
		listeners.add(l);
	}
	
	public void firePlayerAdded(RoutinePlayer player) {
		for (PlayerModelListener playerModelListener : listeners) {
			playerModelListener.playerAdded(player);
		}
	}
		
	public void addPlayer(RoutinePlayer player) {
		playerListModel.addElement(player);
		firePlayerAdded(player);
	}
	
	
	
}
