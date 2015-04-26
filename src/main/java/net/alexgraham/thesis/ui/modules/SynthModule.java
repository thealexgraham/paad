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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.supercollider.synths.grouping.ParamMenuAdapter;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel;
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
	

	JScrollPane scrollPane;
    

	public SynthModule(int width, int height, Synth synth) {
		super(width, height, synth);
		
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
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		JLabel restartLabel = new JLabel("restart");
		restartLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				instance.sendAction("restart");
			}
		});
		
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, synth.getConnector(ConnectorType.ACTION_IN, "restart"), restartLabel));
		middlePanel.add(new JSeparator());
		addParameters(middlePanel);
		
		// Bottom Panel //
		
		bottomPanel.addConnector(Location.BOTTOM, synth.getConnector(ConnectorType.AUDIO_OUTPUT), this);		
	}
	
	public Synth getSynth() {
		return synth;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		synth.close();
	}
	

	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
//		for (ParamModel paramModel : synth.getParamModels()) {
//			if (paramModel.getClass() == DoubleParamModel.class) {
//				panel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
//			}
//			if (paramModel.getClass() == ChoiceParamModel.class) {
//				panel.add(ModuleFactory.createChoiceParamPanel(this, (ChoiceParamModel)paramModel));
//			}
//		}
	}
   
}

