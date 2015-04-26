package net.alexgraham.thesis.supercollider.models;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;

import net.alexgraham.thesis.supercollider.players.PatternPlayer;

public class PlayerModel {
	
	public interface PlayerModelListener {
		public void playerAdded(PatternPlayer player);
		public void playerRemoved(PatternPlayer player);
	}
	
	private CopyOnWriteArrayList<PlayerModelListener> listeners = 
			new CopyOnWriteArrayList<PlayerModelListener>();
	
	private DefaultListModel<PatternPlayer> playerListModel
		= new DefaultListModel<PatternPlayer>();
	
	public PlayerModel() {

	}
	
	// TODO: Rewrite with ArrayList<T>
	public ArrayList<PatternPlayer> getPlayers() {
		ArrayList<PatternPlayer> list = new ArrayList<PatternPlayer>();
		for (Enumeration<PatternPlayer> e = playerListModel.elements(); e.hasMoreElements();)  {
			PatternPlayer player = e.nextElement();
			list.add(player);
		}
		return list;
	}

	public void addListener(PlayerModelListener l) {
		listeners.add(l);
	}
	
	public void firePlayerAdded(PatternPlayer player) {
		for (PlayerModelListener playerModelListener : listeners) {
			playerModelListener.playerAdded(player);
		}
	}
		
	public void addPlayer(PatternPlayer player) {
		playerListModel.addElement(player);
		firePlayerAdded(player);
	}
	
	
	
}
