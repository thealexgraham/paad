package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ChoiceParamModel.ChoiceChangeListener;
import net.alexgraham.thesis.supercollider.synths.parameters.models.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class PatternGenModule extends ModulePanel {
	
	private PatternGen patternGen;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;

	
	
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	ConnectablePanel middlePanel;
	JScrollPane scrollPane;
    
	private JButton closeButton;

	public PatternGenModule(int width, int height, PatternGen patternGen) {
		super(width, height);
		
		this.patternGen = patternGen;
		setInstance(patternGen);
		
		nameLabel = new JLabel(patternGen.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		
		synthdefLabel = new JLabel(patternGen.getDefName());
		idLabel = new JLabel(patternGen.getID());
		
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				patternGen.close();
			}
		});

		setupWindow(this.getInterior());
		
		setSize(getPreferredSize());
		validate();
	}
	
	public PatternGen getPatternGen() {
		return patternGen;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		patternGen.close();
	}
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new ConnectablePanel(new FlowLayout());
		
//		ConnectablePanel connectablePanel = new ConnectablePanel(Location.TOP, patternGen, ConnectorType.ACTION_IN);
//		topPanel.add(connectablePanel);
//		this.addConnectablePanel(connectablePanel);
//		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		// Add pattern out connector
		topPanel.addConnector(Location.TOP, patternGen.getConnector(ConnectorType.PATTERN_OUT));
		this.addConnectablePanel(topPanel);
		
		//Middle Panel//
		middlePanel = new ConnectablePanel();
//		middlePanel.addConnector(Location.LEFT, patternGen.getConnector(ConnectorType.ACTION_IN));
//		this.addConnectablePanel(middlePanel);
//		
		
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		middlePanel.setLayout(new GridLayout(0, 1, 0, 0));
//		middlePanel.add(new JLabel("testing "));
//		middlePanel.add(new JLabel("testing"));
		addParameters();
		//scrollPane = new JScrollPane(middlePanel);
		
		JPanel actionInPanel = new JPanel();
		actionInPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JLabel actionLabel = new JLabel("Generate New");
		actionInPanel.add(actionLabel);
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, patternGen.getConnector(ConnectorType.ACTION_IN), actionInPanel));
		
		//Bottom Panel//

		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //
		bottomPanel.addConnector(Location.BOTTOM, patternGen.getConnector(ConnectorType.PATTERN_OUT));
		this.addConnectablePanel(bottomPanel);

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);
	
	}
	public void addParameters() {
		ArrayList<ParamModel> models = patternGen.getParamModels(); //((PatternGenDef)patternGen.getDef()).getParams());
		for (ParamModel baseModel : models) {
			
			JPanel togetherPanel = new JPanel();
			togetherPanel.setLayout(new BoxLayout(togetherPanel, BoxLayout.LINE_AXIS));
			ConnectablePanel rightPanel = new ConnectablePanel(new FlowLayout(FlowLayout.RIGHT, 0 ,0));
			JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

			if (baseModel.getClass() == IntParamModel.class) {
				IntParamModel model = (IntParamModel) baseModel;
				
				JLabel paramNameLabel = new JLabel(model.getName());
				JSpinner paramSpinner = new JSpinner(model);
				leftPanel.add(paramNameLabel);
				rightPanel.add(paramSpinner);
				
//				ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
//				rightPanel.add(connectablePanel);
//				this.addConnectablePanel(connectablePanel);
				rightPanel.addConnector(Location.RIGHT, model.getConnector(ConnectorType.PARAM_CHANGE_IN));
				this.addConnectablePanel(rightPanel);
				
			} else if (baseModel.getClass() == ChoiceParamModel.class) {
				ChoiceParamModel model = (ChoiceParamModel) baseModel;
				JLabel paramNameLabel = new JLabel(model.getName());
				JLabel paramValueLabel = new JLabel(model.getChoiceName());
				
				model.addChoiceChangeListener(new ChoiceChangeListener() {
					@Override
					public void choiceChanged(String newChoice) {
						paramValueLabel.setText(newChoice);
					}
				});
				
				leftPanel.add(paramNameLabel);
				rightPanel.add(paramValueLabel);
				
				ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, model.getConnector(ConnectorType.CHOICE_CHANGE_IN));
				rightPanel.add(connectablePanel);
				this.addConnectablePanel(connectablePanel);
			}

			togetherPanel.add(leftPanel);
			togetherPanel.add(Box.createHorizontalGlue());
			togetherPanel.add(rightPanel);
			middlePanel.add(togetherPanel);
		}
	}
   
}