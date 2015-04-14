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
	
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	JPanel middlePanel;
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
	
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
		

		//Top Panel//
		
		topPanel = new ConnectablePanel();
		topPanel.setLayout(new FlowLayout());
		topLabel = new JLabel("Routine Player");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		topPanel.addConnector(Location.RIGHT, runner.getConnector(ConnectorType.ACTION_OUT));
		this.addConnectablePanel(topPanel);
		
		ConnectablePanel topWrapper = new ConnectablePanel();
		topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
		topWrapper.add(topPanel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		middlePanel.setLayout(new GridLayout(0, 2));
		createButtons();
		
		addParameters();

		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//
		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //

		pane.add(topWrapper, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	
		

	}
	
	public void addParameters() {
		for (ParamModel paramModel : getInstance().getParamModels()) {
			if (paramModel.getClass() == DoubleParamModel.class) {
				//addDoubleParam((DoubleParamModel) paramModel, middlePanel); 
				middlePanel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
			}
		}
	}
	
	public void createButtons() {
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runner.playOrPause();
			}
		});
		
		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		middlePanel.add(createButtonPanel(playButton, "cycle"));
	
	}
	
	public JPanel createButtonPanel(JButton button, String action) {
		JPanel togetherPanel = new JPanel();
		togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0 ,0));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		ConnectablePanel leftConnectable = new ConnectablePanel(Location.LEFT, runner.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()));
		this.addConnectablePanel(leftConnectable);

		ConnectablePanel rightConnectable = new ConnectablePanel(Location.RIGHT, runner.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()));
		this.addConnectablePanel(rightConnectable);

		panel.add(leftConnectable);
		panel.add(button);
		panel.add(rightConnectable);
		
			
//		togetherPanel.add(panel);
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


}
