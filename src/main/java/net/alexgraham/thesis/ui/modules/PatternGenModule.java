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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Policy.Parameters;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.org.apache.xpath.internal.compiler.Keywords;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.PatternGen;
import net.alexgraham.thesis.supercollider.synths.defs.PatternGenDef;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParam;
import net.alexgraham.thesis.supercollider.synths.parameters.ChoiceParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.DoubleParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.IntParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Param;
import net.alexgraham.thesis.supercollider.synths.parameters.ParamModel;
import net.alexgraham.thesis.supercollider.synths.parameters.Parameter;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class PatternGenModule extends ModulePanel {
	
	private PatternGen patternGen;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;

	
	
	JPanel topPanel;
	JPanel bottomPanel;
	ConnectablePanel middlePanel;
	JScrollPane scrollPane;
    
	private JButton closeButton;

	public PatternGenModule(int width, int height, PatternGen patternGen) {
		super(width, height);
		
		this.patternGen = patternGen;
		
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
		
		topPanel = new JPanel(new FlowLayout());
		
//		ConnectablePanel connectablePanel = new ConnectablePanel(Location.TOP, patternGen, ConnectorType.ACTION_IN);
//		topPanel.add(connectablePanel);
//		this.addConnectablePanel(connectablePanel);
//		
		JLabel topLabel = new JLabel(patternGen.getName());
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new ConnectablePanel();
		middlePanel.addConnector(Location.LEFT, patternGen, ConnectorType.ACTION_IN);
		this.addConnectablePanel(middlePanel);
		
		
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
//		middlePanel.add(new JLabel("testing "));
//		middlePanel.add(new JLabel("testing"));
		addParameters();
		//scrollPane = new JScrollPane(middlePanel);
		
		
		//Bottom Panel//

		bottomPanel = new JPanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		//middlePanel.setBackground(Color.GRAY);
		bottomPanel.setBackground(Color.GRAY);
		
		// Create connectors //
		ConnectablePanel connectablePanel = new ConnectablePanel(Location.BOTTOM, patternGen, ConnectorType.PATTERN_OUT);
		bottomPanel.add(connectablePanel);
		this.addConnectablePanel(connectablePanel);

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
				
//				ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, model, ConnectorType.PARAM_CHANGE_IN);
//				rightPanel.add(connectablePanel);
//				this.addConnectablePanel(connectablePanel);
				rightPanel.addConnector(Location.RIGHT, model, ConnectorType.PARAM_CHANGE_IN);
				this.addConnectablePanel(rightPanel);
				
			} else if (baseModel.getClass() == ChoiceParamModel.class) {
				ChoiceParamModel model = (ChoiceParamModel) baseModel;
				JLabel paramNameLabel = new JLabel(model.getName());
				JLabel paramValueLabel = new JLabel(model.getChoiceName());
				
				leftPanel.add(paramNameLabel);
				rightPanel.add(paramValueLabel);
				
				ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, model, ConnectorType.CHOICE_CHANGE_IN);
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