package net.alexgraham.thesis.ui.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.ui.components.Dial;

public class SynthInfoPanelManual extends JPanel{
	
	public interface SynthInfoPanelDelegate {
		public void synthClosed(Synth synth, SynthInfoPanelManual panel);
	}
	
	private Synth synth;
	private SynthInfoPanelDelegate delegate;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private Dial ampDial;
	private Dial panDial;
	
	private JButton closeButton;
	
	
	public SynthInfoPanelManual (Synth synth) {
		this.synth = synth;
		
		nameLabel = new JLabel(synth.getName());
		synthdefLabel = new JLabel(synth.getSynthName());
		idLabel = new JLabel(synth.getID());
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				delegate.synthClosed(synth, SynthInfoPanelManual.this);
			}
		});
		
		
	}
	
	private void setupPanel() {
		
	}
}
