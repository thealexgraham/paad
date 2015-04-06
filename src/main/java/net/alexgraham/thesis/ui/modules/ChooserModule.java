package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.sun.corba.se.spi.orb.StringPair;

import net.alexgraham.thesis.supercollider.synths.Chooser;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector;
import net.alexgraham.thesis.ui.connectors.ModulePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;

public class ChooserModule extends ModulePanel {
	
	private Chooser chooser;
	ConnectablePanel topPanel;
	ConnectablePanel bottomPanel;
	ConnectablePanel middlePanel;

	public ChooserModule(int width, int height, Chooser chooser) {
		super(width, height);
		
		this.chooser = chooser;
		this.instance = chooser;
		
		setupWindow(this.getInterior());
	}
	public Chooser getChooser() {
		return chooser;
	}
	
	@Override
	public void removeSelf() {
		super.removeSelf();
		chooser.close();
	}

	@Override
	public void setupWindow(Container pane) {
		//pane.setSize(300, 150);
		pane.setLayout(new BorderLayout());
				
		//Top Panel//
		
		topPanel = new ConnectablePanel(new FlowLayout());
		
		JLabel topLabel = new JLabel(chooser.getName());
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//
		middlePanel = new ConnectablePanel();
		middlePanel.setLayout(new GridLayout(0, 1, 5, 5));
		
		middlePanel.addConnector(Location.LEFT, new Connector(chooser, ConnectorType.CHOICE_CHANGE_OUT));
		this.addConnectablePanel(middlePanel);
		
		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		String[] choices = new String[chooser.getChoices().size()];
		choices = chooser.getChoices().toArray(choices);
		
		JComboBox<String> choiceCombo = new JComboBox<String>(choices);
		choiceCombo.setSelectedIndex(chooser.getCurrentIndex());
		
		choiceCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
		        JComboBox cb = (JComboBox)e.getSource();
		        String choice = (String)cb.getSelectedItem();
		        int choiceIndex = cb.getSelectedIndex();
		        chooser.choose(choiceIndex);
			}
		});
		
		middlePanel.add(choiceCombo);

		//Bottom Panel//

		bottomPanel = new ConnectablePanel(new FlowLayout());

		// Set up panels //
		topPanel.setBackground(Color.DARK_GRAY);
		bottomPanel.setBackground(Color.GRAY);


		pane.add(topPanel, BorderLayout.NORTH);
		pane.add(middlePanel, BorderLayout.CENTER);
		pane.add(bottomPanel, BorderLayout.SOUTH);
		
		// Resize based on innards
		setSize(getPreferredSize());
		validate();
		
	}
	
}
