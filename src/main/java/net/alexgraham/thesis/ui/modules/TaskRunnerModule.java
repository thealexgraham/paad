package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.alexgraham.thesis.supercollider.synths.TaskRunner;
import net.alexgraham.thesis.supercollider.synths.TaskRunner.PlayState;
import net.alexgraham.thesis.supercollider.synths.TaskRunner.TaskListener;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class TaskRunnerModule extends ModulePanel implements TaskListener {
	

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
		
		runner.addListener(this);
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
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runner.playOrPause();
			}
		});
		
		Insets currentInsets = playButton.getInsets();
		playButton.setMargin(new Insets(0, currentInsets.left, 0, currentInsets.right));		
		panel.add(createButtonPanel(playButton, "cycle"));

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


	@Override
	public void playStateChanged(PlayState state) {
		playButton.setText("L");
		playButton.paintImmediately(playButton.getVisibleRect());
		switch (state) {
			case READY:
				playButton.setEnabled(true);
				playButton.setText("Play");
				break;
			case PLAYING:
				playButton.setEnabled(true);
				playButton.setText("Stop");
				break;
			case DISABLED:
				playButton.setEnabled(false);
				playButton.setText("Play");
				break;
			default:
				break;
		}
	}
	
	public void setupPanelsOld(ConnectablePanel topPanel,
			ConnectablePanel middlePanel,
			ConnectablePanel bottomPanel) {
		//Top Panel//
		
		JPanel topContent = new JPanel(new FlowLayout());
		
		topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topContent.add(topLabel);

		topContent = ModuleFactory.createSideConnectPanel(this, runner.getConnector(ConnectorType.ACTION_OUT), topContent);
		topContent.setOpaque(false);
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(topContent);
		
		topPanel.addConnector(Location.TOP, runner.getConnector(ConnectorType.ACTION_OUT), this);

		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		JLabel restartLabel = new JLabel("Action");
		restartLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				instance.sendAction("restart");
			}
		});
		
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, instance.getConnector(ConnectorType.ACTION_OUT), restartLabel));
		
		createButtons(middlePanel);
		
		middlePanel.add(new JSeparator());

		JPanel parametersPanel = new JPanel();
//		parametersPanel.setLayout(new GridLayout(0, 1, 5, 5));
		addParameters(middlePanel);
		
		//Bottom Panel//
		bottomPanel = new ConnectablePanel(new FlowLayout());
		bottomPanel.setPreferredSize(new Dimension(bottomPanel.getPreferredSize().width, 5));
		
		bottomPanel.addConnector(Location.BOTTOM, runner.getConnector(ConnectorType.ACTION_OUT), this);
	}


}
