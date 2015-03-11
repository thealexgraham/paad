package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
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
	
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
    

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

		setupWindow(this.getInterior());
		
		// Resize based on innards
		setSize(getPreferredSize());
		validate();
	}
	
	public Synth getSynth() {
		return synth;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		synth.close();
	}
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new ConnectablePanel(new FlowLayout());
		
		JLabel topLabel = new JLabel(synth.getName());
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		addParameters();
		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//

		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);

		bottomPanel.setBackground(Color.GRAY);
		
		bottomPanel.addConnector(Location.BOTTOM, synth.getConnector(ConnectorType.AUDIO_OUTPUT));
		this.addConnectablePanel(bottomPanel);

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	

	}
	public void addParameters() {
		for (ParamModel paramModel : synth.getParamModels()) {
			if (paramModel.getClass() == DoubleParamModel.class) {
				middlePanel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
			}
		}
	}
   
}

