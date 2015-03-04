package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.ParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;
import net.alexgraham.thesis.ui.connectors.ModulePanel;

public class ChangeFuncModule extends ModulePanel {
	
	private ChangeFunc changeFunc;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;
	
	
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	ConnectablePanel middlePanel;
	JScrollPane scrollPane;
    
	private JButton closeButton;
	
	public ChangeFuncModule() {
		init();
	}

	public ChangeFuncModule(int width, int height, ChangeFunc changeFunc) {
		super(width, height);
		this.changeFunc = changeFunc;
		init();
	}
	
	public void init() {
		nameLabel = new JLabel(changeFunc.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		
		synthdefLabel = new JLabel(changeFunc.getSynthName());
		idLabel = new JLabel(changeFunc.getID());
		
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFunc.close();
			}
		});
		
		setupWindow(this.getInterior());
		
		setSize(getPreferredSize());
		validate();
	}
	
	public ChangeFunc getChangeFunc() {
		return changeFunc;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		changeFunc.close();
	}
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new ConnectablePanel(new FlowLayout());
		topPanel.addConnector(Location.TOP, changeFunc.getConnector(ConnectorType.PARAM_CHANGE_OUT));
		this.addConnectablePanel(topPanel);
		
		JLabel topLabel = new JLabel(changeFunc.getSynthName());
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new ConnectablePanel();
		middlePanel.addConnector(Location.LEFT, changeFunc.getConnector(ConnectorType.ACTION_IN));
		this.addConnectablePanel(middlePanel);
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		middlePanel.setLayout(new GridLayout(0, 1));
		
		JButton actionButton = new JButton("Do it");
		actionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFunc.doAction();
			}
		});
		middlePanel.add(actionButton);
//		middlePanel.add(new JLabel("testing "));
//		middlePanel.add(new JLabel("testing"));
		addParameters();
		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//

		bottomPanel = new ConnectablePanel(new FlowLayout());
		bottomPanel.addConnector(Location.BOTTOM, changeFunc.getConnector(ConnectorType.PARAM_CHANGE_OUT));
		this.addConnectablePanel(bottomPanel);
		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //
//		connectablePanel = new ConnectablePanel(Location.BOTTOM, changeFunc, ConnectorType.PARAM_CHANGE_OUT);
//		bottomPanel.add(connectablePanel);
//		this.addConnectablePanel(connectablePanel);

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	

	}
	
	public void addParameters() {
		for (ParamModel paramModel : getInstance().getParamModels()) {
			if (paramModel.getClass() == DoubleParamModel.class) {
				addDoubleParam((DoubleParamModel) paramModel); 
			}
		}
	}
	
	public void addDoubleParam(DoubleParamModel model) {
		JLabel paramNameLabel = new JLabel(model.getName());
		JLabel paramValueLabel = new JLabel(String.valueOf(model.getDoubleValue()));
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				paramValueLabel.setText(String.valueOf(model.getDoubleValue()));
			}
		});
		
		JPanel paramPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		paramPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		paramPanel.add(paramNameLabel);
		paramPanel.add(paramValueLabel);
		
		middlePanel.add(paramPanel);
	}
}