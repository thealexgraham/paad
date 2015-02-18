package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.org.apache.xpath.internal.compiler.Keywords;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.Parameter;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.components.DoubleBoundedRangeModel;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class EffectModule extends ModulePanel {
	
	private Effect effect;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;
	
	
	JPanel topPanel;
	JPanel bottomPanel;
	JPanel middlePanel;
	JScrollPane scrollPane;
    
	private JButton closeButton;

	public EffectModule(int width, int height, Effect effect) {
		super(width, height);
		
		this.effect = effect;
		
		nameLabel = new JLabel(effect.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		
		synthdefLabel = new JLabel(effect.getSynthName());
		idLabel = new JLabel(effect.getID());
		
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				effect.close();
			}
		});
		
		ampDial = new DialD(effect.getModelForParameterName("gain"));
		ampDial.setBehavior(Dial.Behavior.NORMAL);
		ampDial.setName("Gain");
		
//		panDial = new DialD(effect.getModelForParameterName("pan"));
//		panDial.setBehavior(Dial.Behavior.CENTER);
//		panDial.setName("Pan");
		
		setupWindow(this.getInterior());
	}
	
	public Effect getEffect() {
		return effect;
	}
	
	@Override
	public void removeSelf() {
		
		super.removeSelf();
		effect.close();
	}
	
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new JPanel(new FlowLayout());
		
		ConnectablePanel connectablePanel = new ConnectablePanel(Location.TOP, effect, ConnectorType.AUDIO_INPUT);
		topPanel.add(connectablePanel);
		this.addConnectablePanel(connectablePanel);
		
		JLabel topLabel = new JLabel("Routine Player");
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new JPanel();
		//middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		middlePanel.setLayout(new GridLayout(0, 1));
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
		connectablePanel = new ConnectablePanel(Location.BOTTOM, effect, ConnectorType.AUDIO_OUTPUT);
		bottomPanel.add(connectablePanel);
		this.addConnectablePanel(connectablePanel);

		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);	

	}
	public void addParameters() {
		ArrayList<Parameter> getParameters = effect.getParameters();
		for (Parameter parameter : getParameters) {
			DoubleBoundedRangeModel model = (DoubleBoundedRangeModel) effect.getModelForParameterName(parameter.getName());
			JLabel paramNameLabel = new JLabel(parameter.getName());
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
			
			
			ConnectablePanel connectablePanel = new ConnectablePanel(Location.RIGHT, effect, ConnectorType.AUDIO_INPUT);
			paramPanel.add(connectablePanel);
			this.addConnectablePanel(connectablePanel);
			
			middlePanel.add(paramPanel);
		}
	}
	
    public void setup(Container pane) {

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		
		ConnectablePanel topConnectable = new ConnectablePanel(Location.TOP, effect, ConnectorType.AUDIO_INPUT);
		
		// Top connectable constraints
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 1;
		c.gridwidth = 3;
		c.gridy = 0;
		c.gridx = 1;
		
		topConnectable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pane.add(topConnectable, c);
		addConnectablePanel(topConnectable);
		
		
		// Prepare Title //
		JPanel titlePanel = new JPanel();
		//testPanel.setLayout(new GridLayout(1, 1));
		JLabel titleLabel = new JLabel(this.effect.getSynthName());
		titlePanel.add(titleLabel);
		titlePanel.setBackground(Color.LIGHT_GRAY);
		titlePanel.setOpaque(true);
		
		// Title constraints
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 3;
		c.weighty = 0.5;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(titlePanel, c);
		
		// Add amp dials
		c.gridwidth = 1;
		c.gridy = 2;
		c.gridx = 1;
		c.weightx = 0.5;
		pane.add(ampDial, c);
		
		// Bottom Connectable Constraings
		ConnectablePanel bottomConnectable = new ConnectablePanel(Location.BOTTOM, effect, ConnectorType.AUDIO_OUTPUT);
		c.gridwidth = 3;
		c.gridy = 3;
		c.gridx = 1;
		pane.add(bottomConnectable, c);
		addConnectablePanel(bottomConnectable);
		
		
//		c.gridy = 2;
//		c.gridx = 2;
//		c.weightx = 0.5;
//		pane.add(panDial, c);

		
    }
   
}