package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class SynthModule extends ModulePanel {
	
	private Synth synth;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;
    
	private JButton closeButton;

	public SynthModule(int width, int height, Synth synth) {
		super(width, height);
		
		this.synth = synth;
		
		nameLabel = new JLabel(synth.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		
		synthdefLabel = new JLabel(synth.getSynthName());
		idLabel = new JLabel(synth.getID());
		
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synth.close();
			}
		});
		
		ampDial = new DialD(synth.getModelForParameterName("gain"));
		ampDial.setBehavior(Dial.Behavior.NORMAL);
		ampDial.setName("Gain");
		
		panDial = new DialD(synth.getModelForParameterName("pan"));
		panDial.setBehavior(Dial.Behavior.CENTER);
		panDial.setName("Pan");
		
		setup(this.getInterior());
	}
	
	public Synth getSynth() {
		return synth;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		synth.close();
	}
	
	
	public void otherSetup(Container pane) {
		
	}
	
    public void setup(Container pane) {

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;		

		JPanel titlePanel = new JPanel();
		//testPanel.setLayout(new GridLayout(1, 1));
		JLabel titleLabel = new JLabel(this.synth.getSynthName());
		
		titlePanel.add(titleLabel);
		titlePanel.setBackground(Color.LIGHT_GRAY);
		titlePanel.setOpaque(true);
		//testPanel.setAlignmentX(0.5f);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.weighty = 0.5;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(titlePanel, c);
		
		// Add amp dials
		c.gridwidth = 1;
		c.gridy = 2;
		c.gridx = 0;
		c.weightx = 0.5;
		pane.add(ampDial, c);
		
		c.gridy = 2;
		c.gridx = 2;
		c.weightx = 0.5;
		pane.add(panDial, c);
		
		ConnectablePanel bottomConnectable = new ConnectablePanel(Location.BOTTOM, synth.getConnector(ConnectorType.AUDIO_OUTPUT));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.weighty = 1;
		c.gridy = 3;
		c.gridx = 1;
		pane.add(bottomConnectable, c);
		addConnectablePanel(bottomConnectable);
		

		
    }
   
}

