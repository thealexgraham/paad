package net.alexgraham.thesis.ui.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
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
	public void setupPanels(final ConnectablePanel topPanel,
			final ConnectablePanel middlePanel,
			final ConnectablePanel bottomPanel) {
		
		// TODO Auto-generated method stub
		//Top Panel//

		
		JLabel topLabel = getTitleLabel();
		topLabel.setForeground(Color.WHITE);
		topPanel.add(topLabel);
		
		//Middle Panel//

		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		
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
		
//		middlePanel.add(choiceCombo);

//		JLabel returnLabel = new JLabel(chooser.getDef().getReturnType());
		middlePanel.add(ModuleFactory.createSideConnectPanel(this, chooser.getConnector(ConnectorType.CHOICE_CHANGE_OUT), choiceCombo));
		
		//Bottom Panel//	
		// Resize based on innards
		setSize(getPreferredSize());
		validate();
	}



	
}
