package net.alexgraham.thesis.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;

import javax.print.attribute.Size2DSyntax;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;

import net.alexgraham.thesis.supercollider.Synth;
import net.alexgraham.thesis.supercollider.Synth.SynthListener;
import net.alexgraham.thesis.supercollider.SynthDef.Parameter;
import net.alexgraham.thesis.ui.components.Dial;
import net.alexgraham.thesis.ui.components.JSliderD;
import net.alexgraham.thesis.ui.components.Dial.DialListener;
import net.alexgraham.thesis.ui.components.DialD;
import net.alexgraham.thesis.ui.components.Dial.DialEvent;

public class SynthInfoPanel extends JPanel {
	
	private Synth synth;
	
	// Components
	private JLabel nameLabel;
	private JLabel synthdefLabel;
	private JLabel idLabel;
	private DialD ampDial;
	private DialD panDial;
	
	private JButton closeButton;
	
	private boolean selected = false;
	
	static public Dimension getDefaultSize() {
		return new Dimension(495, 90);
	}
	
	public SynthInfoPanel (Synth synth) {
		this.synth = synth;
				
		nameLabel = new JLabel(synth.getName());
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		
		synthdefLabel = new JLabel(synth.getSynthName());
		idLabel = new JLabel(synth.getID());
		
		closeButton = new JButton("X");
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synth.close();
			}
		});
		


		ampDial = new DialD(synth.getModelForParameterName("amp"));
		ampDial.setBehavior(Dial.Behavior.NORMAL);
		ampDial.setName("Amp");
		
		panDial = new DialD(synth.getModelForParameterName("pan"));
		panDial.setBehavior(Dial.Behavior.CENTER);
		panDial.setName("Pan");

		setupLayout();
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if (selected) {
			setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		} else {
			setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		}
	}
	public boolean getSelected() {
		return selected;
	}
	
	public Synth getSynth() {
		return this.synth;
	}
	
	public void setupLayout() {
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(nameLabel, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
						.addComponent(synthdefLabel)
						.addComponent(idLabel))
					.addPreferredGap(ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
					.addComponent(ampDial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panDial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(closeButton)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(closeButton)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panDial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(nameLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(synthdefLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(idLabel)
							.addGap(81))
						.addComponent(ampDial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(81))
		);
		setLayout(groupLayout);

		setPreferredSize(getDefaultSize());
	}

}
	


