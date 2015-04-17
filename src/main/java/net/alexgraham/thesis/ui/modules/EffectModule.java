package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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

	
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	JPanel middlePanel;
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
	
	public Effect getEffect() {
		return effect;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		effect.close();
	}
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new ConnectablePanel(new FlowLayout());
		topPanel.addConnector(Location.TOP, effect.getConnector(ConnectorType.AUDIO_INPUT));
		this.addConnectablePanel(topPanel);
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		addParameters();
		
		//Bottom Panel//

		bottomPanel = new ConnectablePanel(new FlowLayout());
		bottomPanel.addConnector(Location.BOTTOM, effect.getConnector(ConnectorType.AUDIO_OUTPUT));
		this.addConnectablePanel(bottomPanel);
		
		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		bottomPanel.setBackground(Color.GRAY);


		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);
		
		// Resize based on innards
		setSize(getPreferredSize());
		validate();

	}
	
	public void addParameters() {
		for (ParamModel paramModel : getInstance().getParamModels()) {
			if (paramModel.getClass() == DoubleParamModel.class) {
				//addDoubleParam((DoubleParamModel) paramModel, middlePanel); 
				middlePanel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
			}
		}
	}

//    public void setup(Container pane) {
//
//		pane.setLayout(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//
//		c.fill = GridBagConstraints.BOTH;
//		
//		ConnectablePanel topConnectable = new ConnectablePanel(Location.TOP, effect, ConnectorType.AUDIO_INPUT);
//		
//		// Top connectable constraints
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.weighty = 1;
//		c.gridwidth = 3;
//		c.gridy = 0;
//		c.gridx = 1;
//		
//		topConnectable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		pane.add(topConnectable, c);
//		addConnectablePanel(topConnectable);
//		
//		
//		// Prepare Title //
//		JPanel titlePanel = new JPanel();
//		//testPanel.setLayout(new GridLayout(1, 1));
//		JLabel titleLabel = new JLabel(this.effect.getSynthName());
//		titlePanel.add(titleLabel);
//		titlePanel.setBackground(Color.LIGHT_GRAY);
//		titlePanel.setOpaque(true);
//		
//		// Title constraints
//		c.fill = GridBagConstraints.BOTH;
//		c.gridwidth = 3;
//		c.weighty = 0.5;
//		c.weightx = 1;
//		c.gridx = 0;
//		c.gridy = 1;
//		pane.add(titlePanel, c);
//		
//		// Add amp dials
//		c.gridwidth = 1;
//		c.gridy = 2;
//		c.gridx = 1;
//		c.weightx = 0.5;
//		pane.add(ampDial, c);
//		
//		// Bottom Connectable Constraings
//		ConnectablePanel bottomConnectable = new ConnectablePanel(Location.BOTTOM, effect, ConnectorType.AUDIO_OUTPUT);
//		c.gridwidth = 3;
//		c.gridy = 3;
//		c.gridx = 1;
//		pane.add(bottomConnectable, c);
//		addConnectablePanel(bottomConnectable);
//		
//		
////		c.gridy = 2;
////		c.gridx = 2;
////		c.weightx = 0.5;
////		pane.add(panDial, c);
//
//		
//    }
//   
}