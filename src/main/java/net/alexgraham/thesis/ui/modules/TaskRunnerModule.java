package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.alexgraham.thesis.supercollider.synths.TaskPlayer.TaskListener;
import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class TaskRunnerModule extends ModulePanel {
	

	JScrollPane scrollPane;
	
	JLabel topLabel;
	
	TaskRunner runner;
	
	String synthName;
	
	JButton playButton;
	JLabel instLabel;
	
	int lastInt = 0;

	
	public TaskRunnerModule(TaskRunner runner) {
		super();
		
		this.runner = runner;
		this.instance = runner;
		
		setupWindow(this.getInterior());
		setSize(getPreferredSize());
	
		//setPreferredSize(getPreferredSize());
		//revalidate();
	}
	
	@Override
	public void setupPanels(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		//Top Panel//

		topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);

		topPanel.addConnector(Location.TOP, runner.getConnector(ConnectorType.ACTION_OUT), this);

		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
//		middlePanel.add(ModuleFactory.createSideConnectPanel(this, instance.getConnector(ConnectorType.ACTION_OUT), restartLabel));
		
		createButtons(middlePanel);
		
		middlePanel.add(new JSeparator());

		JPanel parametersPanel = new JPanel();
//		parametersPanel.setLayout(new GridLayout(0, 1, 5, 5));
		addParameters(middlePanel);
		
		//Bottom Panel//
		bottomPanel.setPreferredSize(new Dimension(bottomPanel.getPreferredSize().width, 5));
		bottomPanel.addConnector(Location.BOTTOM, runner.getConnector(ConnectorType.ACTION_OUT), this);
	}
	
	
	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
	}
	
	public void createButtons(JPanel panel) {
		playButton = new JButton("Run");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runner.sendPlay();
			}
		});
		
		Insets currentInsets = playButton.getInsets();
		playButton.setMargin(new Insets(0, currentInsets.left, 0, currentInsets.right));		
		panel.add(createButtonPanel(playButton, "start"));

	}
	
	public JPanel createButtonPanel(JButton button, String action) {
		JPanel panel = ModuleFactory.createSideConnectPanel(this, runner.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()), button);
		return panel;
		
	}
	@Override
	public void removeSelf() {
		super.removeSelf();
		runner.close();
	}
}