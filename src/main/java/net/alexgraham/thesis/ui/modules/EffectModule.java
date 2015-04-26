package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class EffectModule extends ModulePanel {
	
	private Effect effect;
	
	// Components
	private JLabel nameLabel;

	

	JScrollPane scrollPane;
    
	private JButton closeButton;

	public EffectModule(int width, int height, Effect effect) {
		super(width, height);
		
		this.effect = effect;
		this.instance = effect;
		//effect.getDef().createFileDef();
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				effect.close();
			}
		});
		
		setupWindow(this.getInterior());
	}
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		
		topPanel.addConnector(Location.TOP, effect.getConnector(ConnectorType.AUDIO_INPUT), this);
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		//middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		addParameters(middlePanel);
		
		//Bottom Panel//

		bottomPanel.addConnector(Location.BOTTOM, effect.getConnector(ConnectorType.AUDIO_OUTPUT), this);	
	}
	
	public Effect getEffect() {
		return effect;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		effect.close();
	}
		
	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
	}

}