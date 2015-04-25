package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.alexgraham.thesis.supercollider.synths.SpecialAction;
import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.supercollider.synths.TaskRunner.PlayState;
import net.alexgraham.thesis.supercollider.synths.TaskRunner.TaskListener;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
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
	
	
	public void setupWindow(Container pane) {
		JPanel topPanel;
		ConnectablePanel bottomPanel;
		JPanel middlePanel;
		
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());

		//Top Panel//
		
		JPanel topContent = new JPanel();
		
		topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topContent.add(topLabel);
		
		topPanel = topContent; //ModuleFactory.createSideConnectPanel(this, specialAction.getConnector(ConnectorType.ACTION_OUT), topContent);

		
		//Middle Panel//
		middlePanel = new JPanel();
		
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		middlePanel.add(Box.createVerticalStrut(1));
		
		createButtons(middlePanel);

		middlePanel.add(Box.createVerticalStrut(1));

		//Bottom Panel//
		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
//		pane.add(bottomPanel, BorderLayout.SOUTH);	
		
		pane.revalidate();
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
