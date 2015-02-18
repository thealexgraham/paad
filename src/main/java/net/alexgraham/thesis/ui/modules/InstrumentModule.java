package net.alexgraham.thesis.ui.modules;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import sun.net.www.content.audio.x_aiff;
import net.alexgraham.thesis.supercollider.synths.Instrument;
import net.alexgraham.thesis.supercollider.synths.Synth;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.connectors.ConnectablePanel;
import net.alexgraham.thesis.ui.connectors.ModulePanel;
import net.alexgraham.thesis.ui.connectors.Connector.ConnectorType;
import net.alexgraham.thesis.ui.connectors.Connector.Location;

public class InstrumentModule extends ModulePanel {

	private Instrument instrument;

	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;

	private JButton closeButton;

	public InstrumentModule(int width, int height, Instrument instrument) {
		super(width, height);

		this.instrument = instrument;

		nameLabel = new JLabel(instrument.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));

		synthdefLabel = new JLabel(instrument.getSynthName());
		idLabel = new JLabel(instrument.getID());

		closeButton = new JButton("X");

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				instrument.close();
			}
		});

		ampDial = new DialD(instrument.getModelForParameterName("gain"));
		ampDial.setBehavior(Dial.Behavior.NORMAL);
		ampDial.setName("Gain");

		panDial = new DialD(instrument.getModelForParameterName("pan"));
		panDial.setBehavior(Dial.Behavior.CENTER);
		panDial.setName("Pan");

		setup(this.getInterior());
	}

	public Instrument getInstrument() {
		return instrument;
	}

	@Override
	public void removeSelf() {

		super.removeSelf();
		instrument.close();
	}

	public void setup(Container pane) {

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;

		ConnectablePanel topConnectable = new ConnectablePanel(Location.TOP,
				instrument, ConnectorType.INST_PLAY_IN);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.gridy = 0;
		c.gridx = 1;
		topConnectable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pane.add(topConnectable, c);
		addConnectablePanel(topConnectable);

		JPanel titlePanel = new JPanel();
		// testPanel.setLayout(new GridLayout(1, 1));
		JLabel titleLabel = new JLabel(this.instrument.getSynthName());

		titlePanel.add(titleLabel);
		titlePanel.setBackground(Color.LIGHT_GRAY);
		titlePanel.setOpaque(true);
		// testPanel.setAlignmentX(0.5f);

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
		c.gridx = 0;
		c.weightx = 0.5;
		pane.add(ampDial, c);

		c.gridy = 2;
		c.gridx = 2;
		c.weightx = 0.5;
		pane.add(panDial, c);

		ConnectablePanel bottomConnectable = new ConnectablePanel(
				Location.BOTTOM, instrument, ConnectorType.AUDIO_OUTPUT);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.weighty = 1;
		c.gridy = 3;
		c.gridx = 1;
		pane.add(bottomConnectable, c);
		addConnectablePanel(bottomConnectable);

	}

}
