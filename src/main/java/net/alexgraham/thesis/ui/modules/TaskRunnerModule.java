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
	
	JPanel topPanel;
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
		
		JPanel topContent = new JPanel(new FlowLayout());
		
		topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topContent.add(topLabel);

		topContent = ModuleFactory.createSideConnectPanel(this, runner.getConnector(ConnectorType.ACTION_OUT), topContent);
		topContent.setOpaque(false);
		
		topPanel = new ConnectablePanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(topContent);
		
		ConnectablePanel connectableTop = (ConnectablePanel) topPanel;
//		connectableTop.addConnector(Location.TOP, runner.getConnector(ConnectorType.ACTION_IN, "cycle"));
		connectableTop.addConnector(Location.TOP, runner.getConnector(ConnectorType.ACTION_OUT));
		
		this.addConnectablePanel(connectableTop);
		
		//Middle Panel//
		middlePanel = new JPanel();	
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
		
//		middlePanel.add(parametersPanel);
		
		//middlePanel.add(Box.createVerticalStrut(5));

		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//
		bottomPanel = new ConnectablePanel(new FlowLayout());;
		bottomPanel.setPreferredSize(new Dimension(bottomPanel.getPreferredSize().width, 5));
		
		bottomPanel.addConnector(Location.BOTTOM, runner.getConnector(ConnectorType.ACTION_OUT), this);
		
		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	
		
		pane.revalidate();
	}
	
	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
//		for (ParamModel paramModel : getInstance().getParamModels()) {
//			if (paramModel.getClass() == DoubleParamModel.class) {
//				//addDoubleParam((DoubleParamModel) paramModel, middlePanel); 
//				panel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
//			}
//		}
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
		
//		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		panel.add(createButtonPanel(playButton, "cycle"));

	}
	
	public JPanel createButtonPanel(JButton button, String action) {

//		button.setBorder(null);

		JPanel panel = ModuleFactory.createSideConnectPanel(this, runner.getConnector(ConnectorType.ACTION_IN, action.toLowerCase()), button);
		
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
