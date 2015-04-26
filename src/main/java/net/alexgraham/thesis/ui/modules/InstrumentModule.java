package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class InstrumentModule extends ModulePanel {
	
	private Instrument synth;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;
    
	private JButton closeButton;
	

	JScrollPane scrollPane;
    

	public InstrumentModule(int width, int height, Instrument synth) {
		super(width, height, synth);
		
		this.synth = synth;

		setupWindow(this.getInterior());
		
		// Resize based on innards
		setSize(getPreferredSize());
		validate();
	}
	
	public Instrument getInstrument() {
		return synth;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		synth.close();
	}
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {

		//Top Panel//
				
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		topPanel.addConnector(Location.TOP, synth.getConnector(ConnectorType.INST_PLAY_IN));
		this.addConnectablePanel(topPanel);
		
		//Middle Panel//

		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		addParameters(middlePanel);
	
		//Bottom Panel//
		bottomPanel.addConnector(Location.BOTTOM, synth.getConnector(ConnectorType.AUDIO_OUTPUT), this);
	}

	public void addParameters(JPanel panel) {
		for (ParamModel paramModel : synth.getParamModels()) {
			if (paramModel.getClass() == DoubleParamModel.class) {
				panel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
			}
		}
	}


   
}

