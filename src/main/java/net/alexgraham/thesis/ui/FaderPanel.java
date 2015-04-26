package net.alexgraham.thesis.ui;

import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.alexgraham.thesis.supercollider.synths.Effect;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.ConnectorUI.Location;

public class FaderPanel extends JPanel {
	private ArrayList<ConnectablePanel> connectables = new ArrayList<ConnectablePanel>();
	
	public ArrayList<ConnectablePanel> getConnectablePanels() {
		return connectables;
	}

	public void addConnectablePanel(ConnectablePanel panel) {
		connectables.add(panel);
	}
	
	public FaderPanel() {

	}
	
	public void setup(ArrayList<Effect> faders, Effect master) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		System.out.println("Setting up");
		for (Effect fader : faders) {
			add(createPanelForFader(fader));
		}
		
		add(Box.createHorizontalStrut(15));
		add(createPanelForFader(master));
	}
	
	public void setup(Effect master) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		System.out.println("Setting up");
		add(createPanelForFader(master));
		
		repaint();
		revalidate();
	}
	
	public JPanel createPanelForFader(Effect fader) {
		ConnectablePanel panel = new ConnectablePanel();
		panel.addConnector(Location.TOP, fader.getConnector(ConnectorType.AUDIO_INPUT));
		this.addConnectablePanel(panel);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel(fader.getName()));
		DialD ampDial = new DialD((BoundedRangeModel) fader.getModelForParameterName("masterGain"));
		panel.add(ampDial);
		
		return panel;
	}
	
}
