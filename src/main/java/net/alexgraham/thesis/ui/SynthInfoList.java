package net.alexgraham.thesis.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.alexgraham.thesis.supercollider.Synth;

public class SynthInfoList extends JPanel {
	
	public interface SynthSelectListener {
		public void selectSynth(Synth synth);
		public void deselectSynth();
	}
	
	private CopyOnWriteArrayList<SynthSelectListener> listeners;
	
	public SynthInfoList() {
		
		listeners = new CopyOnWriteArrayList<SynthInfoList.SynthSelectListener>();
				
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.WHITE);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					// Nothing is selected
					for (Component comp : SynthInfoList.this.getComponents()) {
						 SynthInfoPanel infoPanel = (SynthInfoPanel) comp;
						 infoPanel.setSelected(false);
					}
					
					// Tell listeners to deselect
					for (SynthSelectListener listener : listeners) {
						listener.deselectSynth();
					}
				}
			}
		});
	}
	
	public void addListener(SynthSelectListener listener) {
		listeners.add(listener);
	}
	
	public void addSynthInfoPanel(Synth synth) {
		SynthInfoPanel infoPanel = new SynthInfoPanel(synth);
		infoPanel.setMaximumSize(SynthInfoPanel.getDefaultSize());
		
		infoPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				SynthInfoPanel panel = (SynthInfoPanel) evt.getSource();
				if (evt.getClickCount() == 1) {
					
					// Tell listeners we are selecting a synth
					for (SynthSelectListener listener : listeners) {
						listener.selectSynth(synth);
					}
					
					for (Component comp : getComponents()) {
						 SynthInfoPanel infoPanel = (SynthInfoPanel) comp;
						 infoPanel.setSelected(false);
					}
					panel.setSelected(true);
				}
			}
		});
		
		add(infoPanel);
	}
	
	public void removeSynth(Synth synth) {
		for (Component comp : getComponents()) {
			 SynthInfoPanel infoPanel = (SynthInfoPanel) comp;
			 if (infoPanel.getSynth() == synth) {
				 
				 if (infoPanel.getSelected()) {
					 //TODO: remove it from the list
				 }
				 
				 remove(infoPanel);
				 revalidate();
				 repaint(50L);
			 }
		}
	}
	
	
}
