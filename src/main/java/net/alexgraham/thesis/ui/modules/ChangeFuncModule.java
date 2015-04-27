package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.alexgraham.thesis.supercollider.synths.ChangeFunc;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.supercollider.synths.parameters.models.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.models.ParamModel;
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
	
	JScrollPane scrollPane;
    
	private JButton closeButton;
	
	public ChangeFuncModule() {
		init();
	}

	public ChangeFuncModule(int width, int height, ChangeFunc changeFunc) {
		super(width, height);
		this.changeFunc = changeFunc;
		setInstance(changeFunc);
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
	

	@Override
	public void setupPanels(final ConnectablePanel topPanel,
			final ConnectablePanel middlePanel,
			final ConnectablePanel bottomPanel) {
		
		topPanel.addConnector(Location.TOP, changeFunc.getConnector(ConnectorType.PARAM_CHANGE_OUT), this);
		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
	
		JButton actionButton = new JButton("Action");
		actionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFunc.doAction();
			}
		});

		actionButton.setMargin(new Insets(1, 4, 1, 4));
		
		JPanel buttonPanel = ModuleFactory.createSideConnectPanel(this, changeFunc.getConnector(ConnectorType.ACTION_IN), actionButton);
		
		middlePanel.add(buttonPanel);
		addParameters(middlePanel);
		middlePanel.add(Box.createVerticalStrut(5));
		
		
		//Bottom Panel//
		bottomPanel.addConnector(Location.BOTTOM, changeFunc.getConnector(ConnectorType.PARAM_CHANGE_OUT), this);

	}
	
	public void addParameters(JPanel panel) {
		ModuleFactory.addModelParameters(getInstance().getParamModels(), this, panel);
//		for (ParamModel paramModel : getInstance().getParamModels()) {
//			if (paramModel.getClass() == DoubleParamModel.class) {
//				//addDoubleParam((DoubleParamModel) paramModel); 
//				panel.add(ModuleFactory.createDoubleParamPanel(this, (DoubleParamModel)paramModel));
//			}
//		}
	}


}