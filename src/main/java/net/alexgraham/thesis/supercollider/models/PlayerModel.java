package net.alexgraham.thesis.supercollider.models;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.supercollider.players.RoutinePlayer;

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
	
	// TODO: Rewrite with ArrayList<T>
	public ArrayList<RoutinePlayer> getPlayers() {
		ArrayList<RoutinePlayer> list = new ArrayList<RoutinePlayer>();
		for (Enumeration<RoutinePlayer> e = playerListModel.elements(); e.hasMoreElements();)  {
			RoutinePlayer player = e.nextElement();
			list.add(player);
		}
		return list;
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
