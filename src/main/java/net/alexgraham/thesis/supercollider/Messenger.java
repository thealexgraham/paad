package net.alexgraham.thesis.supercollider;

import java.util.ArrayList;

import net.alexgraham.thesis.supercollider.models.DefModel.MessageListener;

public interface Messenger {
	public void addMessageListener(String message, MessageListener listener);
	public void removeMessageListener(String message, MessageListener listener);
}
