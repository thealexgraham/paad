package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.alexgraham.thesis.supercollider.synths.SpecialAction;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class SpecialActionModule extends ModulePanel {
	

	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	SpecialAction specialAction;
	
	String synthName;
	
	JButton playButton;
	JLabel instLabel;
	
	int lastInt = 0;

	
	public SpecialActionModule(SpecialAction special) {
		super();
		
		this.specialAction = special;
		this.instance = special;
		
		setupWindow(this.getInterior());
		setSize(getPreferredSize());
	
		//setPreferredSize(getPreferredSize());
		//revalidate();
	}
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		
		topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		middlePanel.add(Box.createVerticalStrut(1));
		
		createButtons(middlePanel);

		middlePanel.add(Box.createVerticalStrut(1));
		
	}
	
	public void createButtons(JPanel panel) {
		
		playButton = new JButton(specialAction.getAction().replace("Action", ""));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				specialAction.doAction();
			}
		});
		
		JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		Insets currentInsets = playButton.getInsets();
//		playButton.setMargin(new Insets(1, currentInsets.left, 1, currentInsets.right));
		playButton.setMargin(new Insets(1, 2, 1, 2));
//		flowPanel.add(playButton);
		flowPanel = ModuleFactory.createSideConnectPanel(this, specialAction.getConnector(ConnectorType.ACTION_OUT), playButton);
		panel.add(flowPanel);
	
	}
	
	public JPanel createButtonPanel(JButton button, String action) {

		JPanel panel = ModuleFactory.createSideConnectPanel(this, specialAction.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()), button);
		
		return panel;
		
	}
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		specialAction.close();
	}




}
