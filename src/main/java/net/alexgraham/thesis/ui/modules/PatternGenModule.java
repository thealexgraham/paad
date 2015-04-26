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
import javax.swing.JSeparator;
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
	
	public void setupPanels(ConnectablePanel topPanel, 
			ConnectablePanel middlePanel, 
			ConnectablePanel bottomPanel) {
		
		//Top Panel//
		topPanel.setLayout(new FlowLayout());
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		// Add pattern out connector
		topPanel.addConnector(Location.TOP, patternGen.getConnector(ConnectorType.CHOICE_CHANGE_OUT), this);		
		
		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
		addParameters(middlePanel);
		
		//scrollPane = new JScrollPane(middlePanel);
		
		JPanel actionInPanel = new JPanel();
		actionInPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		JLabel actionLabel = new JLabel("Generate New");
		actionInPanel.add(actionLabel);
		JPanel actionPanel = ModuleFactory.createSideConnectPanel(this, patternGen.getConnector(ConnectorType.ACTION_IN), actionInPanel);
		middlePanel.add(actionPanel);
		
		//Bottom Panel//
		
		// Create connectors //
		bottomPanel.addConnector(Location.BOTTOM, patternGen.getConnector(ConnectorType.CHOICE_CHANGE_OUT));
		this.addConnectablePanel(bottomPanel);
	}
	

	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
//		ArrayList<ParamModel> models = patternGen.getParamModels(); //((PatternGenDef)patternGen.getDef()).getParams());
//		for (ParamModel baseModel : models) {
//
//			if (baseModel.getClass() == IntParamModel.class) {
//				IntParamModel model = (IntParamModel) baseModel;
//				panel.add(ModuleFactory.createIntParamPanel(this, model));
//			} 
//			else if (baseModel.getClass() == ChoiceParamModel.class) {
//				ChoiceParamModel model = (ChoiceParamModel) baseModel;
//				panel.add(ModuleFactory.createChoiceParamPanel(this, model));
//			}
//			
//			panel.add(new JSeparator());
//
//		}
	}
   
}